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
	* 좋아요 등록/삭제
	---------------------------------------------------*/
	$('#output_fav').click(function(){
		//서버와 통신
		$.ajax({
			url:'writeFav',
			type: 'post',
			data: JSON.stringify({board_num: $('#output_fav').attr('data-num')}),
			contentType: 'application/json;charset=utf-8',
			dataType:'json',
			// CSRF 토큰 추가
			          beforeSend: function (xhr) {
			              xhr.setRequestHeader(
			                  $('meta[name="csrf-header"]').attr('content'),
			                  $('meta[name="csrf-token"]').attr('content')
			              );
			          },
					  success:function(param)
					  {
						if(param.result == "logout")
							{
								alert('로그인 후 사용하세요');
							}
							else if(param.result =="success")
								{
									displayFav(param);
								}
								else{
									alert('좋아요 등록/삭제 오류 발생');
								}
					  },
					  error: function(xhr,status,error){
							try{
								//서버에서 보낸 메세지 표시
								const responseJson = JSON.parse(xhr.responseText);
								alert(responseJson.message);
							}
							catch(e)
							{
								//json이 아닌 데이터
								alert('네트워크 오류 발생');
							}
							console.error('Error:', xhr.status, xhr.responseText);
					  }
		})
		
	})
	
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