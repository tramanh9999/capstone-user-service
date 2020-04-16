package com.storyart.userservice.service;

import com.storyart.userservice.common.constants.RoleName;
import com.storyart.userservice.dto.ResultDto;
import com.storyart.userservice.dto.UserProfileDto;
import com.storyart.userservice.exception.BadRequestException;
import com.storyart.userservice.model.Role;
import com.storyart.userservice.model.Story;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.PagedResponse;
import com.storyart.userservice.payload.PasswordChangeRequest;
import com.storyart.userservice.payload.UserInManagementResponse;
import com.storyart.userservice.payload.UserProfileUpdateRequest;
import com.storyart.userservice.repository.RoleRepository;
import com.storyart.userservice.repository.UserRepository;
import com.storyart.userservice.security.UserPrincipal;
import com.storyart.userservice.util.AppContants;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 26/2 ref from pro userController
 */
@Service
public class UserServiceImpl implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public void create(User us) {
        us.setPassword(passwordEncoder.encode(us.getPassword()));
        userRepository.save(us);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void setStatus(boolean status, int id) {


        Optional<User> byId = userRepository.findById(id);


        if (byId.isPresent()) {
            User user = byId.get();

            if (user.isDeactiveByAdmin()) {
                return;
            }
            user.setActive(status);
            userRepository.save(user);
        }
    }

    @Override
    public void setStatusByAdmin(boolean status, int uid) {
        Optional<User> byId = userRepository.findById(uid);
        if (byId.isPresent()) {
            User us = byId.get();
            // neu setStatusByAdmin(true) then turn oin account by set deactive = false
            if (status == true) {
                us.setDeactiveByAdmin(false);
            } else {
                us.setDeactiveByAdmin(true);
            }
            userRepository.save(us);
        }


    }


    //this used for search user of admin and sysadmin.
    // data responsed depend on T of PageResponse
    @Override
    public PagedResponse<User> getAllUser(UserPrincipal userPrincipal, int page, int size) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        //dưa paging vào repo để láy dữ liệu
        Page<User> users = userRepository.findAll(pageable);

        if (users.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), users.getNumber(),
                    users.getSize(), users.getTotalElements(),
                    users.getTotalPages(), users.isLast());


        } else {
            List<User> userList = new ArrayList<>();
            for (User us : users) {
                userList.add(us);
            }
            return new PagedResponse<>(userList, users.getNumber(),
                    users.getSize(), users.getTotalElements(),
                    users.getTotalPages(), users.isLast());

        }
    }

    /**
     * Tim kiem admin theo
     *
     * @param search ky tu can tim
     * @param page   so thu tu page tu list pages- phan ra bang size tu list nhung user tim duoc
     * @param size   so luong ket qua hien thi trong 1 page
     * @return PageResponse  1 chuoi json chua cac thong tin 1 page ( ben trong co list user tuong
     * ung voi page)@see PageResponse
     * @sort createdAt sort theo thoi gian
     */
    @Override
    public PagedResponse<UserInManagementResponse> findByUsernameOrEmail(int page, int size, String search) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        Page<User> userPage = userRepository.findByUsernameLike(search, pageable);
        List<User> usersList = userPage.toList();

        List<UserInManagementResponse> users = convertUserlist(usersList);

        return new PagedResponse<UserInManagementResponse>(users, userPage.getNumber(), userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(), userPage.isLast());
    }

    @Override
    public PagedResponse<Story> findStoriesByUserId(Integer id) {
        return null;
//todo: get Stories by id feign client

//todo hoi ve chuyen trang va validation
    }

    /**
     * Tim kiem admin theo
     *
     * @param search ky tu can tim
     * @param page   so thu tu page tu list pages- phan ra bang size tu list nhung admin tim duoc
     * @param size   so luong ket qua hien thi trong 1 page
     * @return PageResponse  1 chuoi json chua cac thong tin 1 page ( ben trong co list user tuong
     * ung voi page)@see PageResponse
     * @sort createdAt sort theo thoi gian
     */
    @Autowired
    EntityManager entityManager;

    @Override
    public PagedResponse<UserInManagementResponse> findAdminbyUsernameOrEmail(int page, int size, String search) {
        page = page - 1;
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());


        Page<User> userPage = userRepository.findByRoleNameUsernameOrEmail(search, RoleName.ROLE_ADMIN.toString(), pageable);
        List<User> usersList = userPage.toList();

        List<UserInManagementResponse> users = convertUserlist(usersList);

        return new PagedResponse<UserInManagementResponse>(users, userPage.getNumber() + 1, userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(), userPage.isLast());
    }


    public List<UserInManagementResponse> convertUserlist(List<User> users) {
        List<UserInManagementResponse> convertUserlist = new ArrayList<>();
        for (User u : users) {
            convertUserlist.add(new UserInManagementResponse(u));
        }
        return convertUserlist;
    }

    @Override
    public PagedResponse<UserInManagementResponse> findOnlyUserByUsernameOrEmail(int page, int size, String searchtxt) {
        page = page - 1;
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        Page<User> userPage = userRepository.findByRoleNameUsernameOrEmail(searchtxt, RoleName.ROLE_USER.toString(), pageable);
        List<User> usersList = userPage.toList();
        List<UserInManagementResponse> users = convertUserlist(usersList);
        return new PagedResponse<UserInManagementResponse>(users, 1 + userPage.getNumber(), userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(), userPage.isLast());
    }

    @Override
    public void createDefaultSysAdmin() {
        Optional<User> found = userRepository.findByUsername("systemadmin");
        if (found.isPresent()) return;
        User user = new User();
        user.setUsername("systemadmin");
        user.setPassword("12345678");
        user.setName("systemadmin");
        user.setEmail("systemadmin@gmail.com");
        Optional<Role> role = roleRepository.findRoleByName(RoleName.ROLE_SYSTEM_ADMIN);
        user.setRoleId(role.get().getId());
        create(user);
    }

    @Override
    public void createTestUser() {

    }

    @Override
    public void updateAvatar(Integer uid, String link) {
        User user = findById(uid);
        user.setAvatar(link);
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(Integer uid, String link) {
        User user = findById(uid);
        user.setProfileImage(link);
        userRepository.save(user);
    }

    @Override
    public ResultDto getUserProfile(int userId) {
        ResultDto result = new ResultDto();
        result.setSuccess(false);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            result.getErrors().put("NOT_FOUND", "Không tìm thấy tài khoản này trong hệ thống");
        } else if (!user.isActive() || user.isDeactiveByAdmin()) {
            result.getErrors().put("DELETED", "Tài khoản này đã bị xóa");
        } else {
            user.setPassword(null);
            UserProfileDto userProfileDto = modelMapper.map(user, UserProfileDto.class);
            Role role = roleRepository.findRoleById(user.getRoleId()).orElse(null);
            userProfileDto.setRole(role);
            result.setSuccess(true);
            result.setData(userProfileDto);
        }
        return result;
    }

    @Override
    public boolean changePassword(String password, int userId) {
        try {
            User us = findById(userId);
            us.setPassword(passwordEncoder.encode(password));
            userRepository.save(us);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Trang không dưới 0");
        }

        if (size > AppContants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Số lượng trong  một trang không quá " + AppContants.MAX_PAGE_SIZE);

        }
    }

    @Override
    public void update(Integer uid, UserProfileUpdateRequest us) {


        User byId = findById(uid);
//5
        byId.setName(us.getName());
        byId.setEmail(us.getEmail());
        byId.setIntroContent(us.getIntro_content());


        userRepository.save(byId);
    }

    //todo mark role_ to above method


    @Override
    public List<User> findAll() {

        return userRepository.findAll();
    }

    //    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public User findById(Integer id) {

        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    //    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
/*
    public T orElse(T var1) {
        return this.value != null ? this.value : var1;
    }*/


        return optionalUser.orElse(null);

    }

    @Override
    public User findByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.orElse(null);

    }
//todo add admin user method




}
