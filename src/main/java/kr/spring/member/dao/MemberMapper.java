package kr.spring.member.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	//회원관리 - 일반회원
	@Select("select spmember_seq.nextval from dual")
	public Long selectMemNum();
	@Insert("insert into spmember (mem_num, id, nick_name) values(#{mem_num},#{id}, #{nick_name, jdbcType=VARCHAR})")
	public void insertMember(MemberVO member);
	public void insertMemberDetail(MemberVO member);
	public MemberVO selectIdAndNickName(Map<String,String> map);
	public MemberVO selectCheckMember(String id);
	@Select("select * from spmember join spmember_detail using(mem_num) where mem_num=#{mem_num}")
	public MemberVO selectMember(Long mem_num);
	@Update("update spmember set nick_name=#{nick_name} where mem_num = #{mem_num}")
	public void updateMember(MemberVO member);
	public void updateMemberDetail(MemberVO member);
	public void updatePassword(MemberVO member);
	public void deleteMember(Long mem_num);
	public void deleteMemberDetail(Long mem_num);
	//자동 로그인 해제
	@Delete("delete from persistent_logins where username = #{id}")
	public void deleteRememberMe(String id);
	//프로필 사진 업데이트
	@Update("update spmember_detail set photo=#{photo}, photo_name=#{photo_name} where mem_num=#{mem_num}")
	public void updateProfile(MemberVO member);
	
}
