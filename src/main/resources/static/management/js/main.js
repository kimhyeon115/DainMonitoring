
/******** 필수 상수 ***********************************************************************************************************/
let downloading = false;	// 다운로드 응답 대기 상태


/* 공용 | 화면 로딩 */
function loadingScreen(confirm, message = ''){
	$('#loading-message').text(message);
	if (confirm) $('#loadingOverlay').show();
	else $('#loadingOverlay').hide();
}


/******** 공용 및 매니지먼트 전용 함수 ************************************************************************************************/

		/* 공용 API 요청 */
		async function PostRequest(endpoint, body = {}, responseType = 'json') {
			try {
				const response = await fetch(`/management/api/${endpoint}`, {
			        method: "POST",
			        headers: { "Content-Type": "application/json" },
			        body: JSON.stringify(body)
			    });
				
				if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
				
				if (responseType === "text") return await response.text();
		        else return await response.json();
				
			} catch (error) {
				console.error("POST 요청 실패:", error);
		        throw error;
			}
		}
		
		
		/* 공용 결과 메시지 팝업 */
		function showMessage(msg) {
		    alert(msg);
		}
		
		
		/* 공용 결과 메시지 알람 */
		function showToast(message) {
			const toastEl = document.getElementById('liveToast');
			const toastBody = toastEl.querySelector('.toast-body');
			toastBody.textContent = message;

			const toast = new bootstrap.Toast(toastEl);
			toast.show();
		}


/******** 화면 로딩시 실행 *******************************************************************************************/

		/* 화면 시작시 실행 이벤트 */
		$(document).ready(function () {
	
			/* menu 이벤트 */
			$('.feat-btn').on('click', function (e) {
			    e.preventDefault();
		
			    const $li = $(this).closest('li');
			    const isActive = $li.hasClass('active');
		
			    $('.sidebar ul li.active')
			        .removeClass('active')
			        .children('.feat-show')
			        .stop(true, true)
			        .slideUp(300);
		
			    if (!isActive) {
			        $li.addClass('active')
			           .children('.feat-show')
			           .stop(true, true)
			           .slideDown(300);
			    }
			});
	
	
			/* url 접속 가능 여부 상태 표기 */
			$('.card.table-card').each(function () {
			    const $card = $(this);
			    if ($card.data('run')) return;
		
			    const $statusText = $card.find('.card-status-text');
		
			    $card.css({
			        border: '1px solid #ffe0db',
			        boxShadow: '5px 5px 15px rgba(255, 62, 29, 0.8)'
			    });
		
			    $statusText.text('서버 점검이 필요합니다').css('color', '#ff3e1d');
			});
	
	
			/* logger 데이터 수집 상태 표기 */
			$('table tbody tr').each(function () {
			    const $row = $(this);
			    const $statusBadge = $row.find('.badge');
			    if ($statusBadge.length === 0) return;
		
			    const finalDateText = $row.children('td').eq(3).text().trim();
			    if (!finalDateText) return;
		
			    const diffHours = (Date.now() - new Date(finalDateText.replace(/-/g, '/'))) / 36e5;
		
			    let state = 'Active';
			    let color = 'bg-label-primary';
		
			    if (diffHours > 24) {
			        state = 'Stopped';
			        color = 'bg-label-danger';
			    } else if (diffHours > 2) {
			        state = 'Delay';
			        color = 'bg-label-warning';
			    }
		
			    $statusBadge
			        .text(state)
			        .removeClass('bg-label-primary bg-label-warning bg-label-danger')
			        .addClass(color);
			});
	
	
			/* 스위치 라벨 설정 */
			$('input.form-check-input').each(function () {
			    const $input = $(this);
			    const labelSelector = `#${$input.attr('id')}_label`;
			    $(labelSelector).text($input.prop('checked') ? 'OPEN' : 'CLOSED');
			});
	
			
			/* logger 설정 드롭메뉴 이벤트 */
			$('.dropdown').hover(
			    function () {
			        $(this).children('.dropdown-menu').stop(true, true).fadeIn(300);
			    },
			    function () {
			        $(this).children('.dropdown-menu').stop(true, true).fadeOut(150);
			    }
			);
	
	
			/* 스크롤 버튼 표시/숨김 이벤트 */
			$(window).on('scroll', function () {
			    const scrollTop = $(this).scrollTop();
			    $('#scrollTopBtn').toggle(scrollTop > 300);
			    $('#clientDownBtn').toggle(scrollTop < 600);
			});

			
			/* 맨 위로 이동 버튼 */
			$('#scrollTopBtn').on('click', function () {
			    $('html, body').animate({ scrollTop: 0 }, 400);
			});

		});


		/* 웹 오픈여부 변경 요청 */
		async function OpenSwitchChange(target) {
			const result = WebOpenStatusVallidation(target);
			if (typeof result === 'boolean') return;
			
			try {
				const res = await PostRequest('openorclose', result);
				
				if (res.header?.messageCd === 200) {
					const labelId = `#${result.targetId}_label`;
					$(labelId).text(result.open ? 'OPEN' : 'CLOSED');
					showToast(res.header.message);
				} else {
					target.checked = !result.open;
					showToast(res.header.message);
				}
			} catch (e) {
				console.error(e);
				showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
			}
		}


		/* 웹 오픈여부 유효검증 + body 반환 */
		function WebOpenStatusVallidation(target) {
			const ds = target.dataset;
			const d = {
				id: ds.place_id,
				open: target.checked,
				page: ds.korname,
				targetId: target.id,
			};

			const message = d.open 
				? `[${d.page}]의 웹페이지를 오픈하시겠습니까?\n※외부 접근이 허용됩니다.`
				: `[${d.page}]의 웹페이지를 닫으시겠습니까?\n※외부 접근이 차단됩니다.`;
			
			if (confirm(`${message}`)) return d;
			else {
				target.checked = !d.open;
				return false;
			}
		}
		

		/* 로거정보 편집폼 클릭 */
		function loggerEdit(el, event) {
			event.preventDefault();
			
			const ds = el.dataset;
			const d = {
				code: ds.loggername,
				name: ds.korname,
				date: ds.installdt,
				cdma: ds.cdmano,
				pc: ds.pcid
			};
			
			$('.screen-blur, .edit-form').removeClass('d-none');
			
			$("#logger-name").text(d.code);
			$("#logger-korname").val(d.name);
			$("#logger-install").val(d.date);
			$("#logger-cdmano").val(d.cdma);
			$("#logger-computer").val(d.pc);
		}
		

		/* 로거정보 편집폼 수정 요청 */
		async function UpdateLoggerEditForm() {
			const result = LoggerFromVallidation();
			if (typeof result === 'string') return showMessage(result);
			if (typeof result === 'boolean') return;
			
			try {
				const res = await PostRequest('logger', result);
		
				if (res.header?.messageCd === 200) {
					showMessage(res.header.message);
					window.location.reload();
				} else {
					showMessage(res.header.message);
				}
			} catch (e) {
				console.error(e);
				showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
			}
		}


		/* 로거정보 편집폼 입력값 유효검증 + body 반환 */
		function LoggerFromVallidation() {
			const code = $("#logger-name").text();
			const name = $("#logger-korname").val().trim();
			const install_dt = $("#logger-install").val();
			const cdma_no = $("#logger-cdmano").val().trim();
			const computer_id = $("#logger-computer").val();
			
			if (!code || !name || !install_dt) return '모든 정보를 입력하세요.';
			if (!confirm('※주의 : 외부 홈페이지에 동일하게 적용됩니다')) return false;
			
			return {
				code,
				name,
				install_dt,
				cdma_no,
				computer_id
			};
		}
		
		
		/* 로거정보 편집폼 취소 */
		function editCancel() {
			$("#logger-name").text('');
			$("#logger-korname").val('');
			$("#logger-install").val('');
			$("#logger-cdmano").val('');
			$("#logger-computer").val('');
			$('.screen-blur, .edit-form').addClass('d-none');
		}

		
		/* 로거정보 분석폼 클릭 */
		async function loggerAalysis(el, event) {
			event.preventDefault();

			const ds = $(el).data();
			const version = ds.version;
			if (version) return showMessage('[이전 버전] 현장은 사용할수 없는 기능입니다.');
			
			loadingScreen(true);
			await new Promise(resolve => setTimeout(resolve, 500))
			
			const loggerId = ds.loggerId;
			$('#analysis-logger').text(ds.loggerCode);
			
			const $html = await PostRequest("sensorinlogger", { loggerId }, "text");
	        const $parsed = $('<div>').html($html);
			$('#analysis-sensor').html($parsed.find('#transition-sensor').html());
			
			const success = await SelectAnalysisChartDate();
						
			if (success) {
				$('.screen-blur, .analysis-form').removeClass('d-none');
			} else {
				return showMessage('조회 가능한 [센서]가 등록되지 않았습니다.');
			}
		}
		
		
		/* 센서 분석 차트 데이터 조회 */
		async function SelectAnalysisChartDate() {
			loadingScreen(true);
			const $el = $('#analysis-sensor');
			const sensorId = $el.val();
			const placeCode = $el.find(':selected').data('place');
			
			if (!sensorId) {
				loadingScreen(false);
				return false;
			} 
			
			const res = await PostRequest("analysisdata", { sensorId, placeCode });

			replaceAnalysisTable(res);
			replaceAnalysisChart(res);
			loadingScreen(false);
			return true;
		}
		
		
		/* 분석 차트 업데이트 */
		function replaceAnalysisChart(res) {
			
			const chartData = res.body.chartData;
			const first = ['Task', 'Hours per Day'];
			const todaySuccess = Number(chartData.today_success) || 0;
			const todayFail = Number(chartData.today_fail) || 0;
			const totalSuccess = Number(chartData.total_success) || 0;
			const totalFail = Number(chartData.total_fail) || 0;
			
			const todayChart = [
				first,
				[`성공: ${todaySuccess}`, todaySuccess],
				[`실패: ${todayFail}`, todayFail],
			];
			const totalChart = [
				first,
				[`성공: ${totalSuccess}`, totalSuccess],
				[`실패: ${totalFail}`, totalFail],
			];
			
			analysisPieChart(todayChart, 'analysis-chart-today', '오늘');
			analysisPieChart(totalChart, 'analysis-chart-total', '전체');
		}
		
		
		/* 분석 테이블 업데이트 */
		function replaceAnalysisTable(res) {
			const data = res.body.totalFail;
			const $tbody = $('#analysis-table-tbody');
			$tbody.empty();
			
			if (!data || data.length === 0) {
		        $tbody.append(`
		            <tr><td colspan="4" style="text-align:center;">조회된 데이터가 없습니다</td></tr>
		        `);
		        return;
		    }
			
			$.each(data, function(_, item) {
		        const row = `
		            <tr>
		                <td>${item.measured ?? ''}</td>
		                <td>${item.raw ?? ''}</td>
		                <td>${item.status ?? ''}</td>
		            </tr>
		        `;
		        $tbody.append(row);
		    });
		}
		
		
		/* 로거 정보 추이폼 닫기 */
		function closeAnalysisForm() {
			$('.screen-blur, .analysis-form').addClass('d-none');
		}
		
		
		/* 로거정보 추이폼 클릭 */
		async function loggerTransition(el, event) {
			event.preventDefault();
			
			const ds = $(el).data();
			const version = ds.version;
			if (version) return showMessage('[이전 버전] 현장은 사용할수 없는 기능입니다.');
			
			loadingScreen(true);
			await new Promise(resolve => setTimeout(resolve, 500))
			
			const loggerId = ds.loggerId;
			$('#transition-logger').text(ds.loggerCode);
			
			const $html = await PostRequest("sensorinlogger", { loggerId }, "text");
	        const $parsed = $('<div>').html($html);
			$('#transition-sensor').html($parsed.find('#transition-sensor').html());
			
			const cycle = ds.cycle;
			$('#transition-sensor').data('cycle', cycle);
			
			const success = await SelectTransitionChartDate(el);
			
			if (success) {
				$('.screen-blur, .transition-form').removeClass('d-none');
			} else {
				return showMessage('조회 가능한 [센서]가 등록되지 않았습니다.');
			}
		}
		
		
		/* 센서 추이 차트 데이터 조회 */
		async function SelectTransitionChartDate(el) {
			loadingScreen(true);
			const $el = $('#transition-sensor');
			const sensorId = $el.val();
			const placeCode = $el.find(':selected').data('place');
			
			if (!sensorId) {
				loadingScreen(false);
				return false;
			} 
			
			const res = await PostRequest("transitiondata", { sensorId, placeCode });
			
			const ds = $(el).data();
			replaceChart(res, ds.cycle);
			loadingScreen(false);
			return true;
		}
		
		
		/* 추이 차트 업데이트 */
		function replaceChart(res, cycle) {
			const dataRows = res.body.sensorData;
			if (dataRows.length == 0) return noDataGraph('chart');
			
		    const chartRows = dataRows.map(row => [new Date(row['date']), row['raw']]);
			const column = [
				{type: 'date', label: '수집일'},
				{type: 'number', label: '수집값 (raw)'}
			];
			
			const firstValue = chartRows[0][1];

			const rawValues = chartRows.map(row => row[1]);
			const dataMax = Math.max(...rawValues);
			const dataMin = Math.min(...rawValues);

			const distanceFromMax = Math.abs(dataMax - firstValue);
			const distanceFromMin = Math.abs(dataMin - firstValue);
			const dataRange = Math.max(distanceFromMax, distanceFromMin);

			const safetyMargin = dataRange * 5;

			const max = firstValue + dataRange + safetyMargin;
			const min = firstValue - dataRange - safetyMargin;
			
			if (cycle)
				transitionChart(column, chartRows, min, max);
			else 
				transitionColumnChart(column, chartRows, max);
		}
		
		
		/* 로거 정보 추이폼 닫기 */
		function closeTransitionForm() {
			$('.screen-blur, .transition-form').addClass('d-none');
		}
		


async function ClientLoggerDownload() {
	
	downloading = true;
	
	try {
		const response = await fetch("/management/excel", {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

		if (response.status === 503) {
			alert("서버 점검중입니다.");
			window.location.href = "/management/";
			return;
		}
		if (response.status === 401) {
			alert("로그인이 필요합니다.");
			window.location.href = "/management/";
			return;
		}
		if (response.status === 204) {
		    alert("기간내 조회된 데이터가 없습니다.");
		    downloading = false;
		    return;
		}
		if (!response.ok) throw new Error("서버 응답 오류");
		
		const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = "data.xlsx";
        if (contentDisposition && contentDisposition.includes("filename=")) {
            fileName = contentDisposition.split('filename=')[1].replace(/"/g, '');
			fileName = decodeURIComponent(fileName);
        }
		
		const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
		downloading = false;
		
	} catch (error) {
		console.log("엑셀 다운로드 실패:", error);
		downloading = false;
	}
}
