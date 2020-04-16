package com.storyart.userservice.service;

import com.storyart.userservice.dto.ResultDto;
import com.storyart.userservice.dto.UserProfileDto;
import com.storyart.userservice.model.Story;
import com.storyart.userservice.model.User;
import com.storyart.userservice.payload.PagedResponse;
import com.storyart.userservice.payload.PasswordChangeRequest;
import com.storyart.userservice.payload.UserInManagementResponse;
import com.storyart.userservice.payload.UserProfileUpdateRequest;
import com.storyart.userservice.security.UserPrincipal;

import java.util.List;

public interface UserService {

    void create(User us);

    void update(Integer uid, UserProfileUpdateRequest us);

    void delete(Integer id);

    void setStatus(boolean status,int id);
    void setStatusByAdmin(boolean status, int uid);

    List<User> findAll();

    User findById(Integer id);

    User findByUsername(String username);

    User findByEmail(String email);

    PagedResponse<User> getAllUser(UserPrincipal userPrincipal, int page, int size);

    PagedResponse<UserInManagementResponse> findByUsernameOrEmail(int page, int size, String search);

    PagedResponse<Story> findStoriesByUserId(Integer id);

    PagedResponse<UserInManagementResponse> findAdminbyUsernameOrEmail(int page, int size, String search);

    PagedResponse<UserInManagementResponse> findOnlyUserByUsernameOrEmail(int page, int size, String searchtxt);

    void createDefaultSysAdmin();
    void createTestUser();

    void updateAvatar(Integer uid, String link);
    void updateProfileImage(Integer uid, String link);



    ResultDto getUserProfile(int userId);

    boolean changePassword(String password, int userId);
}