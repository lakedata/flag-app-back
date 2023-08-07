package com.flag.flag_back.Controller;

import com.flag.flag_back.Dto.FlagDto;
import com.flag.flag_back.Dto.FlagRes;
import com.flag.flag_back.Model.Flag;
import com.flag.flag_back.service.FlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("flag")
public class ResponseContorller {
    private final FlagService flagService;
    //flagMemList status 0대기 1수락 2거절 *이야기할부분: 30분 단위로 일정 비교하여 되는 시간 넣는 DB는 어딤?

    //일정 30분 단위로 체크 연결된flag DB와 비교하는 코드

    @GetMapping("/accept")
    public FlagRes create(@RequestParam(value="flagDto") FlagDto flagDto) {
        try {
            Flag flag = new Flag();
            flag.setName(flagDto.getName());
            flag.setSTime(flagDto.getSTime());
            flag.setETime(flagDto.getETime());
            flag.setCycle(flagDto.getCycle());
            flag.setUserId(flagDto.getUserId());
            flag.setFriendsList(flagDto.getFriendsList());

            Long id = flagService.createFlag(flag);
            return new FlagRes(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //@GetMapping("/reject")

}
