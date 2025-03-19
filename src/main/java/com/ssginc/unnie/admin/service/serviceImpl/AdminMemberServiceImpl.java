package com.ssginc.unnie.admin.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.member.AdminMemberDetailResponse;
import com.ssginc.unnie.admin.dto.member.AdminMemberResponse;
import com.ssginc.unnie.admin.mapper.AdminMemberMapper;
import com.ssginc.unnie.admin.service.AdminMemberService;
import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 관리자페이지 회원관리 인터페이스의 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;


    /**
     * 회원 전체 리스트 조회
     */
    @Override
    public PageInfo<AdminMemberResponse> findAllMembers(int page, int pageSize){
        // 페이지네이션
        PageHelper.startPage(page, pageSize);
        List<AdminMemberResponse> res = adminMemberMapper.findAllMembers();
        return new PageInfo<>(res);
    }

    /**
     * 회원 상세정보 조회
     */
    @Override
    public AdminMemberDetailResponse findMemberDetail(int memberId) {
        AdminMemberDetailResponse res = adminMemberMapper.findMemberDetail(memberId);
        if (res==null) {
            throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return res;
    }
}