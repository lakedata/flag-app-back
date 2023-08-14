package com.flag.flag_back.Model;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Data@Table(name = "FlagTB")
@Entity@Getter
@Setter
public class Flag {
    @Id
    @Column(name = "flagId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "flagName")
    private String name;
    @Column(name = "cycle")
    private String cycle;
    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Day> dayList;
    @Column(name = "min")
    private Integer minTime;
    @Column(name = "place")
    private String place;
    @Column(name = "memo")
    private String memo;
    @Column(name = "userId")
    private Long userId;
    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FlagMember> friendsList;
    @Column(name = "state")
    private boolean state;
    @Column(name = "fixedDate")
    private String fixedDate;

    @ElementCollection
    private List<String> dates;

    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL)
    private List<UserFlagManager> userFlagManagers = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    private TotalDay totalDay;

    @Builder
    public Flag(Long id,String name, String cycle, List<Day> dayList, Integer minTime, String place, String memo, Long userId, List<FlagMember> friendsList, boolean state, String fixedDate) {
        this.id = id;
        this.name = name;
        this.cycle = cycle;
        this.dayList = dayList;
        this.minTime = minTime;
        this.place = place;
        this.memo = memo;
        this.userId = userId;
        this.friendsList = friendsList;
        this.state = state;
        this.fixedDate = fixedDate;
    }

    public List<List<Long>> getCandidates() {
        List<List<Long>> ret = new ArrayList<>();
        int standardIndex = dates.size();

        // 날짜별로 보기 위해
        for (int i = 0; i < dates.size(); i++) {

            // 한 날짜의 모든 시간대를 탐색
            for (int startIndex = standardIndex + i; startIndex < dates.size() * 13;) {
                HashSet<Long> init = new HashSet<>(getAvailableMember(startIndex));
                int cnt = 1;
                int currentIndex = startIndex + standardIndex;
                int standardSize = init.size();

                while(currentIndex < dates.size() * 13) {
                    List<Long> candidate = getAvailableMember(currentIndex);
                    init.addAll(candidate);

                    if (init.size() != standardSize) {
                        break;
                    }

                    cnt++;
                    currentIndex += standardIndex;
                }

                // 최소 시간을 충족한다면 후보로 등록
                if (cnt >= minTime * 2) {
                    ret.add(new ArrayList<>(init));
                }
            }
        }

        return ret;
    }

    public List<Long> getAvailableMember(int index) {
        List<Long> ret = new ArrayList<>();
        for (UserFlagManager userFlagManager : userFlagManagers) {
            if (userFlagManager.getStatus() != FlagStatus.ACCEPT) {
                continue;
            }
            if (userFlagManager.ableOrNot(index)) {
                ret.add(userFlagManager.getUser().getUserId());
            }
        }
        return ret;
    }

    // 가능한 셀 번호 목록을 모두 반환 (ios 요청)
    public List<Integer> getCellIndex() {
        List<Integer> ret = new ArrayList<>();
        for (UserFlagManager userFlagManager : userFlagManagers) {
            if (userFlagManager.getStatus() != FlagStatus.ACCEPT) {
                continue;
            }
            ret.addAll(addAbleCellIndex(userFlagManager));
        }
        return ret;
    }

    public List<Integer> addAbleCellIndex(UserFlagManager userFlagManager) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (userFlagManager.ableOrNot(i)) {
                ret.add(i);
            }
        }
        return ret;
    }
}