package kr.spring.member.service;

import java.util.List;
import java.util.Map;

import kr.spring.member.vo.MemberVO;

public interface MemberService {
	
	public void insertMember(MemberVO member);
	public MemberVO selectIdAndNickName(Map<String,String> map);
	public MemberVO selectCheckMember(String id);
	public MemberVO selectMember(Long mem_num);
	public void updateMember(MemberVO member);;
	public void updatePassword(MemberVO member);
	public void deleteMember(Long mem_num);
	//프로필 사진 업데이트
	public void updateProfile(MemberVO member);
	
	public Integer selectRowcount(Map<String,Object> map);
	public List<MemberVO> selectList(Map<String,Object> map);
	public void updateByAdmin(MemberVO memberVO);
}
