/*======================================================================
 * customjs.js는 기본적으로 jquery 라이브러리를 요구함
 * =====================================================================*/

/* ========================================================================
 * 폼에 입력한 데이터를 객체에 key와 value의 쌍으로 저장하는 메서드
 * ======================================================================== */
jQuery.fn.serializeObject = function() {
    let obj = null;
    try {
        if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
            let arr = this.serializeArray();
            if (arr) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }//if ( arr ) {
        }
    } catch (e) {
        alert(e.message);
    } finally {
    }
 
    return obj;
};

/*======================================================================
 * HTML을 허용하지 않으면서 줄바꿈
 * =====================================================================*/
function customBrNoHtml(str){
	return str.replace(/</g, '&lt;')
			  .replace(/>/g, '&gt;')
			  .replace(/\r\n/g, '<br>')
			  .replace(/\r/g, '<br>')
			  .replace(/\n/g, '<br>');
}
/*======================================================================
 * 숫자 세자리 쉼표 처리하기
 * 
 * 숫자를 입력하면 동적으로 세자리 쉼표 처리하고 숫자 데이터를 저장하는 input 태그를
 * 생성하도록 처리했음. 
 * 등록폼에서는 keyup 처리만 하면 되지만 수정폼에서는 숫자 데이터 수정 없이 submit할 수 있으므로 
 * submit 이벤트 뱔생시 아래 함수를 호출해야 함
 * 
 * 첫번째 파라미터는 입력되는 숫자, 두번째 파라미터는 0 허용 여부(true는 0 허용, false는 0 불허) 
 * =====================================================================*/
function customNumberLocale(target,zero){
	//전송되는 숫자 데이터가 저장될 태그의 id
	const param = target.attr('id')+'_num';
	if($(param).length==0){
		if($('#'+param).length>0) $('#'+param).remove();
		//전송되는 숫자 데이터가 저장될 태그를 생성
		target.after('<input type="hidden" name="'
		                     +target.attr("id")
		                     +'" id="'+param
		                     +'" value="">');
		//UI에 표시된 태그의 데이터가 전송되지 못 하도록 name 속성 제거
		target.removeAttr('name');
	} 
	
	//UI에 표시된 태그의 값이 비어있으면 데이터를 전송해 줄 태그의 값은 0 처리
	if(target.val()==''){
		$('#'+param).val(0);
		return;
	}
	
	if(!zero && target.val()==0){
		$('#'+param).val(0);
		//0을 허용하지 않을 때 0 값을 입력하면 0을 비어있게 처리
		target.val('');
		return;
	}
	
	if(isNaN(Number(target.val().replace(/,/g,'')))){
		alert('숫자만 입력 가능');
		$('#'+param).val(0);
		target.val(target.attr('value'));//태그에 최초 셋팅 데이터를 다시 읽어 옴
		return;
	}
	
	//전송할 값이 입력되는 태그에서는 ,를 제거한 숫자만 처리
	$('#'+param).val(Number(target.val().replace(/,/g,'')));	
	//화면에 표시되는 태그의 값은 세자리 쉼표 표시
	target.val(Number(target.val().replace(/,/g,'')).toLocaleString());
}
/*===================================================================
 * 업로드 이미지 미리보기
 * 파일 업로드 태그의 선택자,업로드되는 이미지가 보여지는 태그 선택자,업로드 허용 확장자,업로드 허용 파일 사이즈
 * ex)
 * customViewImage('#upload','.my-photo',['png','jpg','jpeg','gif'],1024*1024);
 * =================================================================*/
function customViewImage(fileInput,imgSrc,fileTypes,fileSize){
	$(fileInput).attr('data-path',$(imgSrc).attr('src'));//처음 화면에 보여지는 이미지 읽기
	$(fileInput).change(function(){
		let my_photo = this.files[0];
		if(!my_photo){
			customCancelImage(this,imgSrc);
			return;
		}
		
		if(fileTypes.indexOf(my_photo.name.substring(my_photo.name.lastIndexOf(".")+1, my_photo.name.length).toLowerCase()) < 0){
			alert('파일의 확장자는 '+fileTypes+"만 허용합니다.");
			customCancelImage(this,imgSrc);
		    return;
		}
		
		if(my_photo.size > fileSize){
			alert(my_photo.size + 'bytes('+fileSize+'bytes까지만 업로드 가능)');
			customCancelImage(this,imgSrc);
			return;
		}
		
		const reader = new FileReader();
		reader.readAsDataURL(my_photo);
		
		reader.onload=function(){
			$(imgSrc).attr('src',reader.result);
		};
		
		//********프로필 사진 처리할 경우********//
		if($('#photo_choice').length>0) {
			$('#photo_choice').show();
			$('#photo_btn').hide();
		} 
		//********프로필 사진 처리할 경우********//
		
	});//end of change
}
//이미보기 취소시 사용
function customCancelImage(fileInput,imgSrc){
	$(imgSrc).attr('src',$(fileInput).attr('data-path'));
	$(fileInput).val('');
	//********icon-plus처리할 경우********//
	//<input type="file"> 옆에 <span class="icon-plus">+</span>으로 명시되어 있어야 동작
	if($(fileInput).next().attr('class') == 'icon-plus') $(fileInput).next().show();
	//********icon-plus 처리할 경우********//
}
//파일 업로드 후 정보 초기화(파일을 ajax로 업로드할 때 화면 갱신을 위해 사용)
function customInitImage(fileInput,imgSrc){
	$(fileInput).attr('data-path',$(imgSrc).attr('src'));
	$(fileInput).val('');
}

/*======================================================================
 * 전화번호 형식 지정하기
 * 
 * keyup 이벤트로 체크. 첫번째 파라미터는 전화번호, 두번째 파라미터는 - 사용 여부 지정(true - 사용,false - 불허)
 * ====================================================================*/
function customParsePhone(target,isHyphen){
	target.val(target.val().replace(/[^0-9]/g, ''));
    
    if(isHyphen){
	   	if(target.val().indexOf('82') == 0) {
		   //(82), (2 또는 숫자 두 자리),(숫자 한 자리 이상),(숫자 네 자리)
		   target.val(target.val().replace(/(^82)(2|\d{2})(\d+)?(\d{4})$/, '+$1-$2-$3-$4')); // +82
	   	}else if(target.val().indexOf('1') == 0) {
		   //(1 뒤에 숫자 세 자리), (숫자 네 자리)	
	       target.val(target.val().replace(/(^1\d{3})(\d{4})$/, '$1-$2')); // 1588,1577 ....
	   	}
	   	//(02 또는 0504 또는 0505 또는 0 뒤에 숫자 두 자리),(숫자 한 자리 이상),(숫자 네 자리) 
	   	target.val(target.val().replace(/(^02|^0504|^0505|^0\d{2})(\d+)?(\d{4})$/, '$1-$2-$3')); // 02/0504/0505/010/011/031      
	}
}

/*===================================================================
 * 날짜를 문자열 형식으로 변환
 * =================================================================*/
function customTimeString(date) {
	const seconds = Math.floor((new Date() - new Date(date)) / 1000);
	
	let interval = seconds / (60*60*24*365);
	if(interval>=2) return Math.floor(interval) + "년 전";
	if(interval>=1) return "작년";
	
	interval = seconds / (60*60*24*30);
	if(interval>=3) return Math.floor(interval) + "달 전";
	if(interval>=2) return "두 달 전";
	if(interval>=1) return "한 달 전";
	
	interval = seconds / (60*60*24);
	if(interval>=16) return Math.floor(interval) + "일 전";
	if(interval>=15) return "보름 전";
	if(interval>=8) return Math.floor(interval) + "일 전";
	if(interval>=7) return "일주일 전";
	if(interval>=5) return Math.floor(interval) + "일 전";
	if(interval>=4) return "나흘 전";
	if(interval>=3) return "사흘 전";
	if(interval>=2) return "이틀 전";
	if(interval>=1) return "어제";
	
	interval = seconds / (60*60);
	if(interval>=1) return Math.floor(interval) + "시간 전";
	
	interval = seconds / 60;
	if(interval>=1) return Math.floor(interval) + "분 전";		
	if(seconds<0) return "몇 초 후";
	if(seconds<5) return "몇 초 전";
	return seconds + "초 전";
}
/*===================================================================
 * ajax 페이지 숫자 표시하기
 * custumstyle.css 링크 필수
 * 페이지 번호 클릭 이벤트 처리 내장
 * param : 현재 페이지, 총개수,화면에 보여줄 행의 개수,한 화면의 페이지수,페이지가 표시될 태그의 선택자,실행될 목록
 * ex)
 * customPagination(currentPage,count,rowCount,10,'.custom-button',selectList)
 * =================================================================*/
function customPagination(currentPage,count,rowCount,pageSize,selector,funcList){
	//ul 태그 초기화
	$(selector).empty();
	if(count == 0){
		return;
	}
	
	let totalPage = Math.ceil(count/rowCount);
	
	if(currentPage == undefined || currentPage == ''){
		currentPage = 1;
	}
	//현재 페이지가 전체 페이지 수보다 크면 전체 페이지수로 설정
	if(currentPage > totalPage){
		currentPage = totalPage;
	}
	
	//시작 페이지와 마지막 페이지 값을 구하기
	let startPage = Math.floor((currentPage-1)/pageSize)*pageSize + 1;
	let endPage = startPage + pageSize - 1;
	
	//마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
	if(endPage > totalPage){
		endPage = totalPage;
	}
	
	let add='';
	if(startPage>pageSize){
		add += '<a href='+(startPage-1)+'>[이전]</a>';
	}
	
	for(let i=startPage;i<=endPage;i++){
		if(currentPage == i){
			add += '<span>' + i + '</span>';
		}else{
			add += '<a href='+i+'>'+i+'</a>';
		}
	}
	if(endPage < totalPage){
		add += '<a href='+(startPage+pageSize)+'>[다음]</a>';;
	}
	//ul 태그에 생성한 li를 추가
	$(selector).append(add);
	
	//페이지 버튼 이벤트 연결
	const item = document.querySelectorAll(selector+' a');
	item.forEach(function(element,index,array){
		element.onclick=function(event){
			//페이지 번호를 읽어들임
			currentPage = $(this).attr('href');
			//목록 호출
			funcList(currentPage);
			event.preventDefault();
		};
	});
}
