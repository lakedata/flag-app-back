
package com.flag.flag_back.Controller;

import com.flag.flag_back.Dto.ResponseDto;
import com.flag.flag_back.Dto.UserRes;
import com.flag.flag_back.Dto.UserResponse;
import com.flag.flag_back.Model.Friend;
import com.flag.flag_back.Model.User;
import com.flag.flag_back.Repository.UserRepository;
import com.flag.flag_back.config.BaseResponse;
import com.flag.flag_back.config.BaseResponseStatus;
import com.flag.flag_back.jwt.JwtTokenProvider;
import com.flag.flag_back.service.FriendService;
import com.flag.flag_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flag.flag_back.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/List") //닉네임으로 리스트 조회
    @Operation(summary = "닉네임 검색", description = "닉네임으로 유저 검색")
    public BaseResponse<String> getUsersList(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid String name) {
        try {
            String email = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findUserByEmail(email);
            boolean exist = checkUser(token, name);
            UserResponse users = userService.findListByName(name);
            users.setExistFriend(exist);

            String valueOfuser = String.valueOf(users);
            return new BaseResponse<>(valueOfuser);

            //return users;
        } catch (Exception e) {
            return new BaseResponse<>(NICKNAME_SEARCH_ERROR);
        }
    }
    //예외처리했는데 반환값 걸리는 부분 주석
//    @PostMapping("/List") //닉네임으로 리스트 조회
//    @Operation(summary = "닉네임 검색", description = "닉네임으로 유저 검색")
//    public UserResponse getUsersList(@RequestHeader(value = "Authorization", required = false) String token,  @RequestBody @Valid String name) {
//        try {
//            String email = jwtTokenProvider.getUserPk(token);
//            User user = userRepository.findUserByEmail(email);
//            boolean exist = checkUser(token, name);
//            UserResponse users = userService.findListByName(name);
//            users.setExistFriend(exist);
//            return users;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @PostMapping("/add")
    @Operation(summary = "친구 추가", description = "닉네임으로 친구 추가")
    public BaseResponse<String> addFriend(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid String friendName) {
        try {
            String email = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findUserByEmail(email);
            User friend = userRepository.findUserByName(friendName);

            if(checkUser(token, friend.getName()) == true){
                return new BaseResponse<>(ALREADY_FRIEND);
            }
            Friend friendInfo = new Friend();
            friendInfo.setUserId(user.getUserId());
            friendInfo.setUserId2(friend.getUserId());

            Long id = friendService.add(friendInfo);
            System.out.println("친구 추가 완료 - " + id);

            return new BaseResponse<>(ADD_FRIEND);
        } catch (Exception e) {
            return new BaseResponse<>(ADD_FRIEND_ERROR);
        }
    }

    //친구인지 아닌지 검사
    @PostMapping("/checkFriendId") //닉네임으로 리스트 조회
    @Operation(summary = "친구 중복 검사", description = "친구인지 아닌지 검사")
    public boolean checkUser(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid String friendName) {
        try {
            String email = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findUserByEmail(email);
            User friend = userRepository.findUserByName(friendName);

            return friendService.checkFriendById2(user.getUserId(), friend.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //친구 리스트 보여줌.
    @GetMapping("/friendList")
    @Operation(summary = "친구 list 조회", description = "내 친구 목록 조회.")
    public List<UserResponse> getFriendsList(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String email = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findUserByEmail(email);
            return friendService.friendsListById(user.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //내 친구 내에서 검색 - select문을 - where userid = user2Id where userid1 =  myId
    @PostMapping("/friendList/name") //닉네임으로 리스트 조회
    @Operation(summary = "친구 내에서 닉네임 검색", description = "내 친구 리스트에서 닉네임으로 친구 검색")
    public UserResponse searchFriendsList(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid String name) {
        try {
            String email = jwtTokenProvider.getUserPk(token);
            User user = userRepository.findUserByEmail(email);
            return friendService.friendsListByNickName(user.getUserId(), name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //친구 삭제
    @DeleteMapping("/delete")
    @Operation(summary = "친구 삭제", description = "친구 삭제 API")
    public Map<String, Object> delete(@RequestHeader(value = "Authorization", required = false) String token,  @RequestBody @Valid Long fid) {

        String email = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if(friendService.delete(user.getUserId(),fid) > 0) {
            response.put("result", "SUCCESS");
        } else {
            response.put("result", "FAIL");
            response.put("reason", "일치하는 정보가 없습니다. id를 확인해주세요.");
        }

        return response;
    }
}
