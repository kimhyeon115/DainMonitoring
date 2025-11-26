
/* 상태 변수 */
let offset = 0;
let limit = 100;
let allDataLoaded = false;
let scrollEvent = true;


/* 날짜 포맷팅 (yyyy-mm-dd) */
function formatDate(date) {
	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');
	return `${yyyy}-${mm}-${dd}`;
}


/* 두개 날짜 차이 일수 */
function getDateDiffInDays(startDate, endDate) {
    const startDt = new Date(startDate);
    const endDt = new Date(endDate);
    const diffTime = Math.abs(endDt - startDt);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
}


/* 렌더링 이후 실행 함수 */
$(document).ready(function() {
	
	/* 랜더링 시 기본 날짜 설정 */
	const today = new Date();
	const pastDate = new Date();
	pastDate.setDate(today.getDate() - 6);
	const startVal = formatDate(pastDate);
	const endVal = formatDate(today);
	
	
	/* 렌더링 시 기본 일시 텍스트 */
	$('#start').val(startVal).attr('max', endVal);
	$('#end').val(endVal).attr('max', endVal);
	$('#period').text(`${startVal} ~ ${endVal}`);


	/* 달력 값이 없을 시 당일 날짜 설정 */
	$('input[type="date"]').on('change', function() {
		allDataLoaded = true;
		scrollEvent = false;
        if ($(this).val() === "") $(this).val(formatDate(new Date()));
    });
	
	
	/* 네이비게이션 숨김, 노출 */
	$("#sidebarToggle").on("click", function(e) {
	    e.preventDefault();
	    $("body").toggleClass("sb-sidenav-toggled");
	});


	/* 체크박스 택1 */
	$("input[type='checkbox']").on("change", function() {
	    if ($(this).prop("checked")) {
	        $("input[type='checkbox']").not(this).prop("checked", false);
	    }
		fetchAndRender($('#select').val());
	});
	

	/* 조회 버튼 POST 요청 */
    $('#find').on("click", async function() {
		const $btn = $(this);
		$btn.prop('disabled', true);
		await fetchAndRender($('#select').val());
		$btn.prop('disabled', false);
	});
	
	
	/* 네비게이션 클릭 이벤트 */
	$(".find").on("click", function () {
		const select = this.id;
		const title = $('#select').val();
		if (select == title) return;
		if (!validPeriodCheck(6)) return;
		
		$("#layoutSidenav_nav .sb-sidenav a.find").removeClass("active");
		$(this).addClass("active");
		
		$('#select').val(this.id);
		const Text = $(this).find('span').first().text();
		$('#select').attr('data-title', Text);
	    fetchAndRender(this.id);
	});
	
	
	/* 내려받기 버튼 POST 요청 */
    $('#excel_down').on("click", async function() {
		if (!validPeriodCheck(6)) return;
		const $btn = $(this);
		$btn.prop('disabled', true);
		await fetchAndRender($('#select').val(), true);
		$btn.prop('disabled', false);
	});
	
	
	/* 화면 로딩 이벤트 */
	function loadingScreen(confirm){
		if (confirm) $('#loadingOverlay').show();
		else $('#loadingOverlay').hide();
	}
	
	
	/* 요청 파라미터 정의 */
	function getParam() {
		const start = $('#start').val();
		const end = $('#end').val();
		
		let body = {
	       start: start + ' 00:00:00',
	       end: end + ' 23:59:59',
	       average: $('#average').is(':checked'),
	       oclock: $('#oclock').is(':checked'),
		   days: getDateDiffInDays(start, end) + 1
	    };
		return body;
	}
	
	
	/* 조회 POST 요청 */
	async function fetchAndRender(select = null, excel = false, scroll = false) {
		
		if (!validPeriodCheck(6)) return;
		if (scroll && allDataLoaded) return;
		
		allDataLoaded = false;
		scrollEvent = true;
		
		loadingScreen(true);
		const placeCode = $('body').data('page-code');
		const param = getParam();
		param.select = select == null ? $('#select').val() : select;
		param.excel = excel;
		param.scroll = scroll;
		param.placeCode = placeCode;
		
		if (scroll == true) {
			offset += limit;
			param.offset = offset;
			param.limit = limit;
		} else {
			const cumulative = param.select.split(':')[1] == 'graph';
			offset = 0;
			limit = cumulative ? param.days * 24 : 100;
			param.offset = offset;
			param.limit = limit;
		}

		try {
			const response = await fetch(`/${placeCode}/api/data`, {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify(param),
				redirect: 'manual'
	        });

			if (response.status === 0) {
				alert("유효하지 않는 세션입니다.");
				window.location.href = `/${placeCode}/`;
				return;
			}
	        if (!response.ok) throw new Error("서버 응답 오류");
			
			const result = await response.json();
			if (excel) makeExcelDownload(result);
			else updateInResponse(result, scroll);
		} catch (error) {
			alert("잠시후 재시도해주세요");
		} finally {
			loadingScreen(false);
		}
    };
	
	
	/* 화면 로딩후 바로 실행 */
	fetchAndRender($('#select').val(), false);
	
	
	/* 응답 객체로 화면 업데이트 */
	function updateInResponse(res, scroll) {
		$('#period').text(`${$('#start').val()} ~ ${$('#end').val()}`);
		$('#start').attr('min', res.place.installDt);
		$('#end').attr('min', res.place.installDt);

		if ($('#select').val().startsWith($('body').data('page-code'))) {
			$('#searchTitle').text($('#select').attr('data-title'));
			if (res.cumulativeTableData) replaceCumulativeTableData(res.cumulativeTableData);
			else replaceSystemBody(res.sensorBodyData, res.sensorSet, scroll);
			if (res.chartData) replaceIPIChart(res.chartData, res.sensorSet);
			else noGraph(chartId);
		} else {
			$('#searchTitle').text(`${res.sensorSet.sensorName} ${res.sensorSet.sensorCode}`);
			if (!scroll) replaceChart(res.chartData, res.sensorSet);
			replaceBody(res.sensorBodyData, res.sensorSet, scroll);
		}
	}
	
	
	/* 엔드 포인트 파람으로 메뉴 액티브 설정 */
	const urlParams = new URLSearchParams(window.location.search);
	let selectedId = urlParams.get('select');
	const pageCode = $('body').data('page-code');
	if (selectedId.startsWith(pageCode)) selectedId += ':total';

	if (selectedId) {
		const escapedId = $.escapeSelector(selectedId);
	    const target = $(".find.nav-link#" + escapedId);
		if (target.length > 0) {
			$(".find.nav-link").removeClass("active");
	        target.addClass("active");
			
			const parentId = target.closest('.collapse').prev('a.nav-link.collapsed').attr('data-target');
			const numId = parentId.substring(10);
			document.getElementById(numId).click();
		}
	}
	

	/* 스크롤 이벤트 POST 요청 */
	$(window).on('scroll', () => {
		const nearBottom = $(window).scrollTop() + $(window).height() >= $(document).height();
		if (nearBottom) {
			const select = $('#select').val();
			if (scrollEvent) fetchAndRender(select, false, true);
		}
	});
	
	
	/* 현재 화면에 조회 센서 키 저장 */
	const title = $('.active span:first').text().trim();
	$('#searchTitle').text(title);
	$('#select').attr('data-title', title);
	
});


/* 검색 기간 검증 */
function validPeriodCheck(gap) {
	const startDate = new Date($('#start').val());
	const endDate = new Date($('#end').val());
	const maxEndDate = new Date(startDate);
    maxEndDate.setMonth(maxEndDate.getMonth() + gap);

	if (endDate > maxEndDate) {
		alert(
		  `검색 기간은 ${gap}개월 범위내에서만 조회 가능합니다.\n\n` +
		  "더 긴 기간의 데이터가 필요하신 경우,\n" +
		  "기간을 나누어서 조회해 주세요."
		);
	    return false;
	}
	return true;
}


/* 각변위 반환 */
function degreeRatio(val, radius) {
	if (val == 0) return val;
	const value = Math.trunc(Math.abs(radius/val));
	return `1/${value}`;
}


/* 차트 업데이트 */
function replaceChart(chartData, sensor) {
	$(`#${chartId}`).show();
    const dataRows = chartData.slice(1).map(row => [new Date(row[0]), ...row.slice(1)]);
	if (dataRows.length == 0) return noDataGraph(chartId);
	
	const senserTypeId = sensor.sensorTypeId;
	const min = parseFloat(sensor.minGauge);
    const max = parseFloat(sensor.maxGauge);
	const columnNames = chartData[0];
    const column = columnNames.map((name, i) => ({type: i === 0 ? 'datetime' : 'number', label: name}));
	
	if (sensor.cycleCheck) oneSensorLineChart(min, max, column, dataRows, chartId, senserTypeId);
	else oneSensorColumnChart(min, max, column, dataRows, chartId);
}


/* IPI 차트 업데이트 */
function replaceIPIChart(chartData, sensors) {
	$(`#${chartId}`).show();
    const dataRows = chartData.slice(1);
	if (dataRows.length == 0) return noDataGraph(chartId);
	
	const column = chartData[0];
	const min = parseFloat(sensors[0].minGauge);
    const max = parseFloat(sensors[0].maxGauge);

	oneSensorLineChartOfIPI(min, max, column, dataRows, chartId);
}


/* 테이블 센서 바디 업데이트 */
function replaceBody(data, sensorSet, scroll) {
	const avrageYn = $('#average').is(':checked') ? 'average' : 'basic';
	const checkType = sensorSet.cycleCheck;
	const tableHeard = TableSet[checkType][avrageYn];
	const sensorTypeCode = sensorSet.sensorTypeCode;
	const interval = sensorSet.interval;
	const tbody = $('#data_body');
	
	if (!scroll) {
		const thead = $('#data_head').empty();
		const headerTr = $('<tr>');
		for (const col of tableHeard['key'][sensorTypeCode]) {
			headerTr.append(`<th class="text-center" style="min-width: 70px"><strong>${tableHeard['column'][sensorTypeCode][col]}</strong></th>`);
		}
		thead.append(headerTr);
		tbody.empty();
	}

	if (Array.isArray(data) && data.length > 0) {
		if (data.length < limit) {
			allDataLoaded = true;
			scrollEvent = false;
		} else {
			allDataLoaded = false;
			scrollEvent = true;
		}
		for (const row of data) {
			const bodyTr = $('<tr class="data-tr">');
			for (const col of tableHeard['key'][sensorTypeCode]) {
				if (col.endsWith('_txt')) {
					const target = col.replace('angular_txt', 'changed_val');
					const value = degreeRatio(row[target], interval);
					bodyTr.append(`<td class="text-center">${value}</td>`);
				} else bodyTr.append(`<td class="text-center">${row[col]}</td>`);
			}
			tbody.append(bodyTr);
		}
	} else {
		allDataLoaded = true;
		scrollEvent = false;
		if (!scroll) tbody.append(noDataTableBody());
	}
}


/* 누적 그래프 테이블 바디 */
function replaceCumulativeTableData(data) {
    $('#data_head').empty();
    $('#data_body').empty();
	if (!data || data.length === 0) return;
	
	const thead = $('#data_head');
	const tbody = $('#data_body');
	
	const headerTr = $('<tr>');
	data[0].forEach(col => {
		headerTr.append(`<th class="text-center" style="min-width: 100px"><strong>${col.replace(' ', '<br>')}</strong></th>`);
	});
	thead.append(headerTr);	

	for (let i = data.length - 1; i > 0; i--) {
		const row = data[i];
		const bodyTr = $('<tr class="data-tr">');
		row.forEach(col => {
			bodyTr.append(`<td class="text-center">${col}</td>`);
		})
		tbody.append(bodyTr);
	}
	
	if (Array.isArray(data) && data.length > 0) {
		if (data.length - 1 < limit) {
			allDataLoaded = true;
			scrollEvent = false;
		} else {
			allDataLoaded = false;
			scrollEvent = true;
		}
	} else {
		allDataLoaded = true;
		scrollEvent = false;
	}
}


/* 테이블 시스템 바디 업데이트 */
function replaceSystemBody(data, sensorSet, scroll) {
	const avrageYn = $('#average').is(':checked');
	const tbody = $('#data_body');
	
	if (!scroll) {
		const thead = $('#data_head').empty();
		const headerTr = $('<tr>');
		headerTr.append(`<th class="text-center" style="min-width: 70px"><strong>${avrageYn ? '날짜' : '측정일시'}</strong></th>`);
		for (const col of sensorSet) {
			headerTr.append(`<th class="text-center" style="min-width: 70px"><strong>${col.sensorCode}</strong></th>`);
			if (!col.cycleCheck && !col.hidden) {
				headerTr.append(`<th class="text-center" style="min-width: 70px"><strong>kine</strong></th>`);
			}
		}
		thead.append(headerTr);
		tbody.empty();
	}
	
	if (Array.isArray(data) && data.length > 0) {
		if (data.length < limit) allDataLoaded = true;
		else {
			allDataLoaded = false;
			scrollEvent = true;
		}
		for (const row of data) {
			const bodyTr = $('<tr class="data-tr">');
			const key = avrageYn ? 'measurement_date' : 'measured_at';
			bodyTr.append(`<td class="text-center">${row[key]}</td>`);
			for (const col of sensorSet) {
			    const sensorVal = row[col.sensorCode] ?? 'x';
			    bodyTr.append(`<td class="text-center">${sensorVal}</td>`);

			    if (!col.cycleCheck && !col.hidden) {
			        const kineVal = row['kine'] ?? 'x';
			        bodyTr.append(`<td class="text-center">${kineVal}</td>`);
			    }
			}
			tbody.append(bodyTr);
		}
	} else {
		if (!scroll) tbody.append(noDataTableBody());
	}
}


function noDataTableBody() {
	const bodyTr = $('<tr>');
	bodyTr.append(`
		<td style="text-align: center; padding: 75px 0; display: table-cell; width: 100%;" colspan="100%">
			조회된 데이터가 없습니다
		</td>
	`);
	return bodyTr;
}


/* 엑셀 파일 생성 및 다운로드 */
function makeExcelDownload(res) {
	const data = res.sensorBodyData;
	const sensorSet = res.sensorSet;
	
	if (!Array.isArray(data) || data.length === 0) {
		return alert('기간 내 조회된 데이터가 없습니다.');
	}

	const start = $('#start').val();
	const end = $('#end').val();
	const avg = $('#average').is(':checked') ? '_일평균' : '';
	const sensorCode = sensorSet.sensorCode;
	const interval = sensorSet.interval;
	
	let fileName = sensorCode;
	let wb = XLSX.utils.book_new();
	let ws;

	if (Array.isArray(sensorSet)) {
		const rawKeys = Object.keys(data[0]);

		const dateKeys = [];
		if (rawKeys.includes('measured_at')) dateKeys.push('measured_at');
		if (rawKeys.includes('measurement_date')) dateKeys.push('measurement_date');

		const otherKeys = rawKeys
			.filter(k => !dateKeys.includes(k))
			.sort((a, b) => a.localeCompare(b));

		const keys = [...dateKeys, ...otherKeys];

		const headers = keys.map(k => {
			if (k === 'measured_at') return '측정일시';
			if (k === 'measurement_date') return '날짜';
			return k;
		});
		
		const rows = data.map(row =>
			keys.map(k => row[k] != null ? row[k] : '')
		);

		const excelData = [headers, ...rows];
		ws = XLSX.utils.aoa_to_sheet(excelData);
		fileName = $('#select').attr('data-title');
		
		ws = XLSX.utils.aoa_to_sheet(excelData);
	} else {
		const sensorTypeCode = sensorSet.sensorTypeCode;
		const checkType = sensorSet.cycleCheck;
		const avrageYn = $('#average').is(':checked') ? 'average' : 'basic';

		const tableHeard = TableSet[checkType][avrageYn];
		const keys = tableHeard.key[sensorTypeCode];
		const headers = keys.map(key => tableHeard.column[sensorTypeCode][key]);
		const rows = data.map(row => 
			keys.map(k => {
				if (k.endsWith('_txt')) {
					const target = k.replace('angular_txt', 'changed_val');
					return degreeRatio(row[target], interval);
				}
				return row[k] ?? '';
			})
		);
		
		const excelData = [headers, ...rows];

		ws = XLSX.utils.aoa_to_sheet(excelData);
	}
	
	XLSX.utils.book_append_sheet(wb, ws, fileName);
	XLSX.writeFile(wb, `${fileName}&${start}~${end}${avg}.xlsx`);
}
