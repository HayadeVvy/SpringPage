$(function(){
	/*--------------------------------------------------
	* 좋아요 읽기 (좋아요 선택 여부와 선택한 총개수 표시)
	---------------------------------------------------*/
	function selectFav(board_num)
	{
		//서버와 통신
		$.ajax({
			url: 'getFav/' + board_num,
			type: 'get',
			dataType: 'json',
			success:function(param)
			{
				displayFav(param);
				
			},
			error:function()
			{
				alert('네트워크 오류 발생');
			}
		});
	}
	/*--------------------------------------------------
	* 좋아요 표시 공통 함수
	---------------------------------------------------*/
	function displayFav(param)
	{
		let output;
		if(param.status == 'yesFav')
			{
				output = '../assets/images/fav02.gif';
			}
		else if(param.status == 'noFav')
		{
			output = '../assets/images/fav01.gif';	
		}
		else
		{
			output ='../assets/images/fav01.gif';
			alert('좋아요 표시 오류')
		}
		//문서 객체에 추가
		$('#output_fav').attr('src',output);
		$('#output_fcount').text(param.count);
	}
	/*--------------------------------------------------
	* 초기 데이터 호출
	---------------------------------------------------*/
	selectFav($('#output_fav').attr('data-num'));
});