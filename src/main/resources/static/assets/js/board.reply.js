$(function(){
	/************************
	 * 식별자 정의
	 ************************/
	let rowCount = 10; //한 페이지에 보여 줄 댓글 수
	let currentPage; //현재 댓글 페이지 번호
	let count; //전체 댓글수
	/************************
	 * 댓글 목록
	 ************************/
	function fetchReplyList(pageNum)
	{
		currentPage = pageNum;
		//서버와 통신
		$.ajax({
			url:`listReply/${$('#board_num').val()}/${pageNum}/${rowCount}`,
			type: 'get',
			dataType:'json',
			beforeSend:function()
			{
				$('#loading').show();//로딩 이미지 표시
				
			},
			complete:function()
			{
				$('#loading').hide(); //로딩 이미지 숨김
			},
			success:function(param)
			{
				count = param.count;
				
				if(pageNum == 1)
					{
						$('#output').empty();
					}
				
				//댓글수 표시
				updateReplyCount(count);
			
				//댓글 목록 렌더링(param)
				renderReplyList(param.list, param.user_num);
				
				//페이지 버튼 처리
				if(currentPage >= Math.ceil(count/ rowCount))
					{
						$('.paging-button').hide();
					}
				else
				{
					$('.paging-button').show();
				}
			},
			error: function()
			{
				alert("네트워크 오류입니다.");
			}
			
		});
	}
	
	//댓글 수 표시
	function updateReplyCount(count){
		$('#output_rcount').text(`댓글수(${count})`);
	}
	
	//댓글 목록 화면 제어
	function renderReplyList(list, user_num)
	{
		$(list).each(function(index,item)
	{
		if(index > 0)
			{
				$('#output').append('<hr size="1" width="100%">');
			}
			let output = `
				<div class="item">
					<ul class="detail-info">
						<li>
							<img src="../member/viewProfile?mem_num=${item.mem_num}" width="40" height="40" class="my-photo"> 
						</li>
						<li>
							${item.memberVO.userName}<br>
							<span class="modify-date">
								${item.re_mdate ? '최근 수정일: ' + item.re_mdate : '등록일: ' + item.re_date}
							</span>
						</li>
					</ul>
					<div class="sub-item">
						<p>${customBrNoHtml(item.re_content)}</p>
						${renderButtons(item,user_num)}
					</div>
				</div>
			`;
			$('#output').append(output);
		});
	}
	//버튼 표시
	function renderButtons(item, user_num)
	{
		let buttons = '';
		
		if(user_num == item.mem_num)
			{
				buttons += `
					<input type="button" data-num="${item.re_num}" value="수정" class="modify-btn">
					<input type="button" data-num="${item.re_num}" value="삭제" class="delete-btn">
				`;
			}
			
			if(user_num)
				{
					buttons += `
						<input type="button" data-num="${item.re_num}" data-parent="0" data-depth="0" value="답글 작성" class="response-btn">
						
					`;
				}
				
				if(item.resp_cnt >0)
					{
						buttons += `
						<div>
							<input type="button" data-status="0" data-num="${item.re_num}" value="^ 답글 ${item.resp_cnt}" class="response-btn">
						</div>
						`;
					} 
					else{
						buttons += `
						<div>
							<input type="button" data-status="0" data-num="${item.re_num}" value="^ 답글 0" class="response-btn" style="display:none;">
						</div>
						`;
					}
					return buttons;
	}
	
	//다음 댓글 보기 버튼 클릭시 데이터 추가
	$('.paging-button input').click(function(){
		fetchReplyList(currentPage+1);
	})
	
	/************************
	 * 댓글 등록
	************************/
	$('#re_form').submit(function(event){
		//기본 이벤트 제거
		event.preventDefault();
		if($('#re_content').val().trim() == '')
			{
				alert("댓글 입력 필수!");
				$('#re_content').val('').focus();
				return;
			}
			
			const formData = $(this).serializeObject();
			
			//서버와 통신
			$.ajax({
				url: 'writeReply',
				type: 'post',
				data: JSON.stringify(formData),
				contentType: 'application/json;charset=utf-8',
				dataType: 'json',
				beforeSend: function (xhr) {
					xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content')
				);
				},
				success:function(param)
				{
					if(param.result == 'success')
						{
							resetReplyForm();
							fetchReplyList(1);
						}
						else
						{
							alert('댓글 등록 오류 발생');
						}
				},
				error:function(xhr){
					try{
						const responseJson = JSON.parse(xhr.responseText);
						alert(responseJson.message);	
					}
					catch(e)
					{
						//JSON이 아닌경우
						alert('네트워크 오류 발생');
					}
					console.error('Error:', xhr.status, xhr.responseText);
				}
			});
	});
	//댓글 작성 폼 초기화
	function resetReplyForm()
	{
		$('textarea').val('');
		$('#re_first .letter-count').text('300/300');
	}
	/************************
	* 댓글(답글) 등록, 수정 공통
	************************/
	//textarea에 내용 입력시 글자수 체크
	$(document).on('keyup','textarea',function()
{
	
});
	
	
	/************************
	* 초기 데이터(목록) 호출
	************************/
	fetchReplyList(1);
});