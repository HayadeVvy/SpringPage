package kr.spring.member.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberMapper memberMapper;

	@Override
	public void insertMember(MemberVO member) {
		Long mem_num = memberMapper.selectMemNum();
		member.setMem_num(mem_num);
		memberMapper.insertMember(member);
		memberMapper.insertMemberDetail(member);
	}

	

	@Override
	public MemberVO selectIdAndNickName(Map<String, String> map) {
	
		return memberMapper.selectIdAndNickName(map);
	}

	@Override
	public MemberVO selectMember(Long mem_num) {
		return memberMapper.selectMember(mem_num);
	}

	@Override
	public void updateMember(MemberVO member) {
		memberMapper.updateMember(member);
		memberMapper.updateMemberDetail(member);
		
	}

	

	@Override
	public void updatePassword(MemberVO member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMember(Long mem_num) {
		
		
	}


	@Override
	public void updateProfile(MemberVO member) {
		memberMapper.updateProfile(member);
		
	}

	@Override
	public MemberVO selectCheckMember(String id) {
		return memberMapper.selectCheckMember(id);
	}



	@Override
	public Integer selectRowcount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return memberMapper.selectRowCount(map);
	}



	@Override
	public List<MemberVO> selectList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return memberMapper.selectList(map);
	}



	@Override
	public void updateByAdmin(MemberVO memberVO) {
		memberMapper.updateByAdmin(memberVO);
		
	}



	@Override
	public List<MemberVO> selectSearchMember(String id) {
		
		return memberMapper.selectSearchMember(id);
	}

}
