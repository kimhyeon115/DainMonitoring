
/******** 필수 상수 ***********************************************************************************************************/
const formulaValues = [];
const allowed = ["A","B","C","D","E","raw","correction","angle","displace","initial","sin","pi"];
const allowedOperators = ["+", "-", "*", "/", "(", ")"];
const groupResetMap = {
	computer: ['computer'],
	company: ['company'],
	place: ['place'],
	logger: ['logger'],
	sensor: ['sensor', 'applyCalculation'],
	sensorType: ['sensorType', 'sensorTypeSetting'],
	calculation: ['calculation']
};


/******** 공용 및 시스템 전용 함수 ************************************************************************************************/

/* 한국 기준 현재 일시 생성 */
function getKSTFormattedDateTime() {
	const now = new Date();
    const utc = now.getTime() + (now.getTimezoneOffset() * 60000);
    const kstTime = new Date(utc + (9 * 60 * 60 * 1000));

    const year = kstTime.getFullYear();
    const month = String(kstTime.getMonth() + 1).padStart(2, '0');
    const day = String(kstTime.getDate()).padStart(2, '0');

    const hours = String(kstTime.getHours()).padStart(2, '0');
    const minutes = String(kstTime.getMinutes()).padStart(2, '0');
    const seconds = String(kstTime.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}


/* 오늘 일자 생성 */
function getTodayDate() {
	const now = new Date();
    const utc = now.getTime() + (now.getTimezoneOffset() * 60000);
    const kstTime = new Date(utc + (9 * 60 * 60 * 1000));

    const year = kstTime.getFullYear();
    const month = String(kstTime.getMonth() + 1).padStart(2, '0');
    const day = String(kstTime.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}


/* 공용 API 요청 */
async function PostRequest(endpoint, body = {}, responseType = 'json') {
	try {
		const response = await fetch(`/management/system/api/${endpoint}`, {
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


/* 공용 리스트 아이템 선택 처리 */
function selectListItem($el, target, item = '> *') {
	$el.find(item).removeClass('row-select');
	$(target).addClass('row-select');
}


/* 공용 버튼 상태 변경 */
function setButtonState($btn, text, className) {
	$btn.text(text)
		.removeClass("btn-outline-primary btn-outline-warning btn-outline-danger")
		.addClass(className);
}


/* 공용 알파벳만 입력 제한 */
function validateEnglish(el, type) {
	const before = el.value;
	const baseRules = {
		alphabetOnly: { regex: /[^A-Za-z]/g, msg: '영어만 입력 가능합니다.' },
		alphanumericHyphen: { regex: /[^A-Za-z0-9_]/g, msg: '영어, 숫자, 언더바(_)만 입력 가능합니다.' }
	};

	const typeRules = {
		place: baseRules.alphabetOnly,
		logger: baseRules.alphanumericHyphen,
		sensor: baseRules.alphanumericHyphen,
		sensortype: baseRules.alphanumericHyphen
	};

	const rule = typeRules[type];
	if (!rule) return;

	const after = before.replace(rule.regex, '');
	twoValueComparison(el, before, after, rule.msg);
}


/* 공용 입력 전후값 비교 */
function twoValueComparison(el, before, after, msg) {
	if (before !== after) {
		el.value = after;
		el.classList.add("shake");
		el.setCustomValidity(msg);
		el.reportValidity();
		setTimeout(() => el.classList.remove("shake"), 300);
	} else {
		el.setCustomValidity("");
		validateInputChange(el);
	}
}


/* 공용 입력 필드 일괄 초기화 */
function setDefaultInputSettings() {
    document.querySelectorAll("input, textarea").forEach(el => {
        el.setAttribute("autocomplete", "off");
    });

    document.querySelectorAll(".number-input").forEach(input => {
        input.addEventListener("input", e => {
            const val = e.target.value;
            const sanitized = val
                .replace(/[^0-9.\-]/g, "")
                .replace(/(?!^)-/g, "")
                .replace(/\.(?=.*\.)/g, "");
            e.target.value = sanitized;
        });
    });
}


/* 공용 탑메뉴 드롭다운 닫기 */
function closeDropdown(triggerElement) {
    const dropdown = triggerElement?.closest(".dropdown");
    if (!dropdown) return;

    dropdown.classList.remove("show");
    const menu = dropdown.querySelector(".dropdown-menu");
    if (menu) menu.classList.remove("show");

    const toggle = dropdown.querySelector("[data-bs-toggle='dropdown']");
    if (toggle) toggle.setAttribute("aria-expanded", "false");
}


/* 현재 화면 컨텐츠 조회 */
async function loadContent(category, triggerElement) {
	
	const allowed = ["dmsSetting", "moveAndBackup"];
	if (!allowed.includes(category)) {
		showMessage("개발 진행중입니다.");
		const prevCategory = $('#category').val();
		if (prevCategory) await loadContent(prevCategory, triggerElement);
		return;
	}
	
	try {
        const response = await fetch(`/management/system/content?category=${category}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

		const html = await response.text();
		$('main').html(html);

		if (triggerElement) {
			closeDropdown(triggerElement);
			$('#category').val(category);
		}
		
		setDefaultInputSettings();
        if (category === 'dmsSetting') FilterCalculationButton();
		
	} catch (error) {
		console.error('페이지 로딩 실패:', error);
		showMessage('페이지 로딩 중 오류가 발생했습니다.');
	}
}


/* [이동및백업] 데이터 조회 테이블 필터 이벤트 */
function onSelectFilterChange() {
    const pcVal = $('#select_pc').val();
    const parsingVal = $('#select_parsing').val();
    const $rows = $('#collect_table tbody tr');

    $rows.each(function () {
        const $row = $(this);
        const rowPc = $row.attr('data-pc_id');
        const rowParsing = $row.attr('data-parsing_type_id');
        const matchPc = !pcVal || rowPc === pcVal;
        const matchParsing = !parsingVal || rowParsing === parsingVal;
        const shouldShow = matchPc && matchParsing;

        $row.toggle(shouldShow);
    });

    const visibleCount = $rows.filter(':visible').length;
    $('#total_count_row').text('Total ' + visibleCount);
}


/* 계정 권한 검증 */
function LevelValidation() {
	const userLevel = $('meta[name="user"]').attr('content');
	const category = $('#category').val();
	const restricted = {
		moveAndBackup: ['development', 'admin']
	};

	if (!restricted[category]) return true;
	return restricted[category].includes(userLevel);
}


/******** DMS > DMS 설정 ************************************************************************************************/

/* (DMS 공용) 화면 컨텐츠내 선택요소 최신값 요청 */
async function ComboChangePostMethod() {
	const category = $('#category').val();

	try {
		const $html = await PostRequest("combo", { category }, "text");
		const $parsed = $('<div>').html($html);
		$('#computer-ul').html($parsed.find('#computer-ul').html());
		$('#company-ul').html($parsed.find('#company-ul').html());
		$('#place-ul').html($parsed.find('#place-ul').html());
		$('#place-company-id').html($parsed.find('#place-company-id').html());
		$('#logger-place-ul').html($parsed.find('#logger-place-ul').html());
		$('#logger-computer-id').html($parsed.find('#logger-computer-id').html());
		$('#sensor-place-ul').html($parsed.find('#sensor-place-ul').html());
		$('#sensor-type-ul').html($parsed.find('#sensor-type-ul').html());
		$('#sensor-type-place-ul').html($parsed.find('#sensor-type-place-ul').html());
		$('#calculation-ul').html($parsed.find('#calculation-ul').html());
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* (DMS 공용) 컨텐츠 탭타이틀 클릭 */
function HideDmsTitleCard(target) {
    const $t = $(target);
    const groupName = target.dataset.group;
    const $card = $t.closest('.dms-append-card');
    const $content = $card.find('.mini-container, .container, .small-container, .dms-middle-title');
    const $allContents = $('.mini-container, .container, .small-container, .dms-middle-title');
    
    $allContents.not($content).stop(true, true).slideUp(300).removeClass('place-li-show');

    if ($content.is(':visible')) {
        $content.stop(true, true).slideUp(300).removeClass('place-li-show');
        $t.find('img').attr('src', '/management/images/chevrons-right.png');
    } else {
        $content.stop(true, true).slideDown({
            duration: 300,
            start: function() {
                const $this = $(this);
                if ($this.hasClass('mini-container') || $this.hasClass('container') || $this.hasClass('small-container')) {
                    $this.css('display', 'flex');
                    $this.css('flex-direction', 'row');
                    $this.css('gap', '10px');
                } else {
                    $this.css('display', 'flex');
                }
            }
        }).addClass('place-li-show');

        $('.dms-set-title img').attr('src', '/management/images/chevrons-right.png');
        $t.find('img').attr('src', '/management/images/chevrons-down.png');
    }

    if (groupName && groupResetMap[groupName]) {
        groupResetMap[groupName].forEach(id => ResetDmsForm(id, true));
    }
}


/* (DMS 공용) 입력값 중복사용 여부 검증 요청 */
async function DuplicateCheck(target, type) {
	const config = {
        place:      { selector: '#place-code',       key: 'code' },
        logger:     { selector: '#logger-code',      key: 'code' },
        sensor:     { selector: '#sensor-code',      key: 'code' },
        company:    { selector: '#company-full-name', key: 'full_name' },
        computer:   { selector: '#computer-name',    key: 'name' },
        sensortype: { selector: '#sensor-type-code', key: 'code' },
        calculation:{ selector: '#calculation-name', key: 'name' },
    };
	
	const conf = config[type];
    if (!conf) return;
	
	const searchKey = $(conf.selector).val()?.trim();
    if (!searchKey || target.innerText === '사용가능') return;
	
	const body = { type, [conf.key]: searchKey };
	
	const checkPlaceLogger = (selector, message) => {
        const val = $(selector).val();
        if (val === '0') {
            showMessage(message);
            throw new Error('validation');
        }
        return val;
    };
	
	try {
        if (type === 'sensor') {
            const placeId = checkPlaceLogger('#sensor-place-id', '현장을 먼저 선택하세요.');
            checkPlaceLogger('#sensor-logger-id', '해당 현장내 로거정보를 먼저 등록하세요.');
            Object.assign(body, { place_id: placeId});
        } else if (type === 'logger') {
            checkPlaceLogger('#logger-place-id', '현장을 먼저 선택하세요.');
        }

        const res = await PostRequest('duplicate', body);
        CheckingDuplicateResult(target, res.header.messageCd);
    } catch (e) {
        if (e.message === 'validation') return;
        console.error(e);
        showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
    }
}


/* (DMS 공용) 입력값 중복여부 확인 결과 시각적 적용 이벤트 */
function CheckingDuplicateResult(target, messageCd) {
	target.className = "btn btn-6"; 

    if (messageCd === 200) {
        target.innerText = '사용가능';
        target.classList.add("btn-outline-primary");
    } else if (messageCd === 409) {
        target.innerText = '사용불가';
        target.classList.add("btn-outline-danger");
    } else {
        target.innerText = '중복확인';
        target.classList.add("btn-outline-warning");
    }
}


/* (DMS 공용) 입력값 변화시 버튼 초기화 */
function validateInputChange(target) {
	const $btn = $(target).next("a");
	setButtonState($btn, '중복확인', 'btn-outline-warning');
}


/* DMS PC정보 저장,수정,삭제 요청 */
async function UpsertDmsComputerInfoForm(mode) {
	const FORM_TYPE = 'computer';
    const result = DmsComputerFormValidation(mode);

    if (typeof result === 'string') return showMessage(result);

    if (!mode) {
        if (result.id === '0') return ResetDmsForm(FORM_TYPE);
        if (!confirm(`[${result.name}]의 정보를 삭제하겠습니까?`)) return;
    }

    try {
        const res = await PostRequest(FORM_TYPE, result);
        showMessage(res.header?.message || "처리에 실패했습니다.");
    } catch (e) {
        console.error(e);
        showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
    } finally {
        ComboChangePostMethod();
        ResetDmsForm(FORM_TYPE);
    }
}


/* DMS PC정보 입력값 유효검증 + body 반환 */
function DmsComputerFormValidation(mode) {
	const id = $('#computer-id').val();
    const name = $('#computer-name').val().trim();
    const ip = $('#computer-ip').val().trim();
    const description = $('#computer-description').val().trim();
    const duplicateCheck = $('#checking-computer-code').text();

    if (mode) {
        if (!ip || !name) return '*필수 정보* 모두 입력해 주세요.';
        if (duplicateCheck !== '사용가능') return '[PC 명칭] 중복확인 후 저장하세요.';
    }

    return { id, name, ip, description, mode };
}


/* DMS PC정보 내의 PC 리스트 클릭 */
function SelectedComputerLi(target) {
	selectListItem($('#computer-ul'), target, 'li');
	
	const ds = target.dataset;
	const d = {
		id: ds.id,
		name: ds.name,
		ip: ds.ip,
		description: ds.description || ''
	};
	
	$('#computer-id').val(d.id);
	$('#computer-name').val(d.name).prop('disabled', true);
	$('#computer-ip').val(d.ip);
	$('#computer-description').val(d.description);
	
	setButtonState($('#checking-computer-code'), '사용가능', 'btn-outline-primary');
}


/* DMS 업체정보 저장,수정,삭제 요청 */
async function UpsertDmsCompanyInfoForm(mode) {
    const FORM_TYPE = 'company';
    const result = DmsCompanyFormValidation(mode);

    if (typeof result === 'string') return showMessage(result);

    if (!mode) {
        if (result.id === '0') return ResetDmsForm(FORM_TYPE);
        if (!confirm(`[${result.full_name}]의 정보를 삭제하겠습니까?`)) return;
    }

    try {
        const res = await PostRequest(FORM_TYPE, result);
        showMessage(res.header?.message || "처리에 실패했습니다.");
    } catch (e) {
        console.error(e);
        showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
    } finally {
		ComboChangePostMethod();
        ResetDmsForm(FORM_TYPE);
    }
}


/* DMS 업체정보 입력값 유효검증 + body 반환 */
function DmsCompanyFormValidation(mode) {
    const id = $('#company-id').val();
    const full_name = $('#company-full-name').val().trim();
    const short_name = $('#company-short-name').val().trim();
    const tel = $('#company-tel').val().trim();
    const address = $('#company-address').val().trim();
    const duplicateCheck = $('#checking-company-code').text();

    if (mode) {
        if (!full_name || !short_name) return '*필수 정보* 모두 입력해 주세요.';
        if (duplicateCheck !== '사용가능') return '[법인 명칭] 중복확인 후 저장하세요.';
    }

    return { id, full_name, short_name, tel, address, mode };
}


/* DMS 업체정보 내의 현장 리스트 클릭 */
function SelectedCompanyLi(target) {
	selectListItem($('#company-ul'), target, 'li');
	
	const { id, short_name, full_name, tel, address } = target.dataset;
	
	$('#company-id').val(id);
	$('#company-full-name').val(full_name).prop('disabled', true);
	$('#company-short-name').val(short_name);
	$('#company-tel').val(tel);
	$('#company-address').val(address);
	
	setButtonState($('#checking-company-code'), '사용가능', 'btn-outline-primary');
}


/* DMS 현장정보 저장,수정,삭제 요청 */
async function UpsertDmsPlaceInfoForm(mode) {
	const FORM_TYPE = 'place';
	const result = DmsPlaceFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);
	
	if (!mode) {
        if (result.id === '0') return ResetDmsForm(FORM_TYPE);
        if (!confirm(`[${result.short_name}]의 정보를 삭제하겠습니까?`)) return;
    }

	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ComboChangePostMethod();
		ResetDmsForm(FORM_TYPE);
	}
}


/* DMS 현장정보 입력값 유효검증 + body 반환 */
function DmsPlaceFormValidation(mode) {
	const code = $('#place-code').val().trim();
    const short_name = $('#place-short-name').val().trim();
    const full_name = $('#place-full-name').val().trim();
    const url = $('#place-url').val().trim();
    const user = $('#place-user').val().trim();
    const password = $('#place-password').val().trim();
    const duplicateCheck = $('#checking-place-code').text();
	
	if (!code || !short_name || !url || !user || !password) return '*필수 정보* 모두 입력해 주세요.';
	if (duplicateCheck !== '사용가능') return '[현장코드] 중복확인 후 저장하세요';
	
	return {
        id: $('#place-id').val(),
        code,
        short_name,
        full_name: full_name === '' ? short_name : full_name,
        manager: $('#place-manager').val().trim(),
        url,
        user,
        password,
        run: $('#place-run').prop('checked'),
        open: $('#place-open').prop('checked'),
        company_id: $('#place-company-id').val(),
        uid: $('#place-uid').val(),
        mode
    };
}


/* DMS 현장정보 현장코드 입력시 접속정보 자동입력 */
function autoFillConnection(val) {
	if (!val) $('#place-url').val('');
	else $('#place-url').val(`http://dain23.iptime.org/${val}`);
	$('#place-user').val(val);
	$('#place-password').val(val);
}


/* DMS 현장정보 내의 현장 리스트 클릭 */
function SelectedPlaceLi(target) {
	selectListItem($('#place-ul'), target, 'li');
	
	const ds = target.dataset;
	const d = {
		id:          ds.id,
		company_id:  ds.company_id,
		code:        ds.code,
		short_name:  ds.short_name,
		full_name:   ds.full_name,
		manager:     ds.manager || '',
		url:         ds.url || '',
		user:        ds.user || '',
		password:    ds.password || '',
		run:         ds.run === 'true',
		open:        ds.open === 'true',
		uid:         ds.user_id || '0'
	};
	
	$('#place-id').val(d.id);
	$('#place-code').val(d.code).prop('disabled', true);
	$('#place-short-name').val(d.short_name);
	$('#place-full-name').val(d.full_name);
	$('#place-manager').val(d.manager);
	$('#place-url').val(d.url);
	$('#place-user').val(d.user);
	$('#place-password').val(d.password);
	$('#place-run').prop('checked', d.run);
	$('#place-open').prop('checked', d.open);
	$('#place-company-id').val(d.company_id);
	$('#place-uid').val(d.uid);
	
	setButtonState($('#checking-place-code'), '사용가능', 'btn-outline-primary');
}


/* DMS 로거정보 저장,수정,삭제 요청 */
async function UpsertDmsLoggerInfoForm(mode) {
	const FORM_TYPE = 'logger';
	const result = DmsLoggerFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);
	
	if (!mode) {
		if (result.id === '0') return ResetDmsForm(FORM_TYPE);
		if (!confirm(`[${result.code}]의 정보를 삭제하겠습니까?`)) return;
	}

	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
		const category = $('#category').val();
		const $html = await PostRequest("combo", { category, placeId: result.place_id }, "text");
        const $parsed = $('<div>').html($html);
		$('#logger-ul').html($parsed.find('#logger-ul').html());
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ResetDmsForm(FORM_TYPE);
	}
}


/* DMS 로거정보 입력값 유효검증 + body 반환 */
function DmsLoggerFormValidation(mode) {
	const id = $('#logger-id').val();
	const place_id = $('#logger-place-id').val();
	const code = $('#logger-code').val().trim();
	const name = $('#logger-name').val().trim();
	const install_dt = $('#logger-install-dt').val();
	const file_path = $('#logger-file-path').val().trim();
	const duplicateCheck = $('#checking-logger-code').text();
	
	if (place_id === '0') return '[현장]을 선택하세요.';
	if (!code || !name || !install_dt || !file_path) return '*필수 정보* 모두 입력해 주세요.';
	if (duplicateCheck !== '사용가능') return '[로거코드] 중복확인 후 저장하세요';
	
	return {
		id,
		place_id,
		code,
		name,
		location: $('#logger-location').val().trim(),
		cdma_no: $('#logger-cdma-no').val().trim(),
		install_dt,
		file_path,
		computer_id: $('#logger-computer-id').val(),
		cycle_check: $('#logger-cycle-check').val(),
		run: $('#logger-run').prop('checked') ? 1 : 0,
		mode
	};
}


/* DMS 로거정보 내의 현장 리스트 클릭 */
async function SelectedLoggerPlaceLi(target) {
	selectListItem($('#logger-place-ul'), target, 'li');
	
	const category = $('#category').val();
    const placeId = target.dataset.id;
    $('#logger-place-id').val(placeId);
	
	try {
		const $html = await PostRequest("combo", { category, placeId }, "text");
        const $parsed = $('<div>').html($html);
		$('#logger-ul').html($parsed.find('#logger-ul').html());
    	ResetDmsForm('logger');
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* DMS 로거정보 내의 로거 리스트 클릭 */
function SelectedLoggerLi(target) {
	selectListItem($('#logger-ul'), target, 'li');
	
	const ds = target.dataset;
	const d = {
		id: ds.id,
		place_id: ds.place_id,
		code: ds.code,
		name: ds.name,
		location: ds.location || '',
		cdma_no: ds.cdma_no || '',
		computer_id: ds.computer_id,
		install_dt: ds.install_dt,
		file_path: ds.file_path,
		cycle_check: ds.cycle_check === 'true' ? '1' : '0',
		run: ds.run === 'true'
	};

	$('#logger-id').val(d.id);
	$('#logger-code').val(d.code).prop('disabled', true);
	$('#logger-name').val(d.name);
	$('#logger-location').val(d.location);
	$('#logger-cdma-no').val(d.cdma_no);
	$('#logger-computer-id').val(d.computer_id);
	$('#logger-install-dt').val(d.install_dt);
	$('#logger-file-path').val(d.file_path);
	$('#logger-cycle-check').val(d.cycle_check);
	$('#logger-run').prop('checked', d.run);
	setButtonState($('#checking-logger-code'), '사용가능', 'btn-outline-primary');
}


/* DMS 로거정보 내의 로거 리스트중에서 중복여부 확인 */
function validateCheckLoggerLi(el) {
	el.value = el.value.replace(/\s/g, '');
	const inputCode = el.value;
	const exists = $('#logger-ul li').toArray().some(li => li.dataset.code === inputCode);
	
	if (exists) {
		el.classList.add('shake');
		el.setCustomValidity('이미 사용중인 코드입니다.');
		el.reportValidity();
		setTimeout(() => el.classList.remove('shake'), 300);
	} else {
		el.setCustomValidity('');
	}
}


/* DMS 센서정보 저장,수정,삭제 요청 */
async function UpsertDmsSensorInfoForm(mode) {
	const FORM_TYPE = 'sensor';
	const result = DmsSensorFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);
	
	if (!mode) {
		if (result.id === '0') return ResetDmsForm(FORM_TYPE);
		if (!confirm(`[${result.code}]의 정보를 삭제하겠습니까?`)) return;
	}
	
	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
		
		const category = $('#category').val();
		const $html = await PostRequest("combo", { category, placeId: result.place_id }, "text");
        const $parsed = $('<div>').html($html);
		$('#sensor-ul').html($parsed.find('#sensor-ul').html());
		
		const $combo = $('#sensor-logger-combo');
		$combo.val(result.logger_id);
		$combo.trigger('change');
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ResetDmsForm(FORM_TYPE);
	}
}


/* DMS 센서정보 입력값 유효검증 + body 반환 */
function DmsSensorFormValidation(mode) {
	const id = $('#sensor-id').val();
	const place_id = $('#sensor-place-id').val();
	const logger_id = $('#sensor-logger-id').val();
	const code = $('#sensor-code').val().trim();
	const name = $('#sensor-name').val().trim();
	const install_dt = $('#sensor-install-dt').val().replace('T', ' ');
	const sensor_type_id = $('#sensor-sensor-type').val();
	const sensor_order = $('#sensor-order').val();
	const duplicateCheck = $('#checking-sensor-code').text();
	
	if (place_id === '0' || logger_id === '0') return '[현장, 로거]를 먼저 선택하세요.';
	if (!code || !name || !install_dt || !sensor_type_id || !sensor_order) return '*필수 정보* 모두 입력해 주세요.';
	if (duplicateCheck !== '사용가능') return '[센서코드] 중복확인 후 저장하세요';
	
	return {
		id,
		place_id,
		logger_id,
		code,
		name,
		location: $('#sensor-location').val(),
		sensor_type_id,
		install_dt,
		sensor_order,
		mode
	};
}


/* DMS 센서정보 내의 현장 리스트 클릭 */
async function SelectedSensorPlaceLi(target) {	
	selectListItem($('#sensor-place-ul'), target, 'li');
	
	const category = $('#category').val();
    const placeId = target.dataset.id;
	$('#sensor-place-id').val(placeId);
    $('#sensor-place-code').val(target.dataset.code);
	
	try {
		const $html = await PostRequest('combo', { category, placeId }, 'text');
		const $parsed = $('<div>').html($html);
		$('#sensor-logger-combo').html($parsed.find('#sensor-logger-combo').html());
        $('#sensor-ul').html($parsed.find('#sensor-ul').html());

        const $combo = $('#sensor-logger-combo');
        const optionCount = $combo.find('option').length;
        $combo.find(`option:eq(${optionCount > 1 ? 1 : 0})`).prop('selected', true).trigger('change');
		ResetDmsForm('sensor');
        ResetDmsForm('applyCalculation', true);
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* DMS 센서정보 로거콤보 변경시 센서 필터 */
function validateSensorLoggerChange(el) {
	const loggerId = $(el).val();
	const $sensorList = $('#sensor-ul');
	const $items = $sensorList.find('li.sensor-item');

	let visibleCount = 0;
	$items.each(function () {
		const isVisible = $(this).data("logger_id") == loggerId;
		$(this).toggleClass('sensor-li-hidden', !isVisible);
		if (isVisible) visibleCount++;
	});
	
	$sensorList.find('.no-sensor-msg').remove();
	if (visibleCount === 0) {
		$sensorList.append('<li class="no-sensor-msg">센서를 추가하세요</li>');
	}

	const $selected = $('#sensor-logger-combo option:selected');
	$('#sensor-logger-id').val(loggerId);
	$('#sensor-logger-code').val($selected.data('code'));
	
	ResetDmsForm('sensor');
}


/* DMS 센서정보 내의 센서 리스트 클릭 */
async function SelectedSensorLi(target) {
	selectListItem($('#sensor-ul'), target, 'li');
	
	const ds = target.dataset;
	const d = {
		id: ds.id,
		code: ds.code,
		name: ds.name,
		location: ds.location,
		install_dt: ds.install_dt,
		sensor_type_id: ds.sensor_type_id,
		sensor_order: ds.sensor_order
	};
	
	$('#sensor-id').val(d.id);
	$('#sensor-code').val(d.code);
	$('#sensor-name').val(d.name);
	$('#sensor-location').val(d.location);
	$('#sensor-install-dt').val(d.install_dt);
	$('#sensor-sensor-type').val(d.sensor_type_id);
	$('#sensor-order').val(d.sensor_order);
	setButtonState($('#checking-sensor-code'), '사용가능', 'btn-outline-primary');
	
	ResetDmsForm('applyCalculation');
	ResetDmsForm('sensorInitial');
	$('#initial-sensor-code').val(d.code);
	
	const comboBody = {
		category: $('#category').val(),
		placeId: $('#sensor-place-id').val(),
		sensorId: d.id
	};
	
	try {
		const $html = await PostRequest('combo', comboBody, 'text');
		const $parsed = $('<table>' + $html + '</table>');
	    $('#apply-calculation-body').html($parsed.find('#apply-calculation-body').html());
		setCalculationOfParam();
		$('#sensor-initial-body').html($parsed.find('#sensor-initial-body').html());
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* DMS 센서정보 적용 계산식 저장 요청 */
async function InsertDmsSensorApplyCalculation() {
	const result = DmsSensorApplyCalculationFormValidation();
	if (typeof result === 'string') return showMessage(result);
	
	try {
		const res = await PostRequest('applycalculation', result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
					
		const comboBody = {
			category: $('#category').val(),
			placeId: $('#sensor-place-id').val(),
			sensorId: result.sensor_id
		};
		
		const $html = await PostRequest('combo', comboBody, 'text');
		const $parsed = $('<table>' + $html + '</table>');
	    $('#apply-calculation-body').html($parsed.find('#apply-calculation-body').html());
		setCalculationOfParam();
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ResetDmsForm('applyCalculation');
	}
}


/* DMS 센서정보 적용 계산식 입력값 유효검증 + body 반환 */
function DmsSensorApplyCalculationFormValidation() {
	const sensor_id = $('#sensor-id').val();
	const target = $('#sensor-target-column').val();
	const calculation_item_id = $('#sensor-apply-calculation option:selected').data('id') || '';
	const from_dt = $('#sensor-calculation-start').val().replace('T', ' ');
	const to_dt = $('#sensor-calculation-end').val()?.replace('T', ' ') || null;
	
	if (sensor_id === '0') return '[현장]과 [센서]를 먼저 선택하세요.';
	if (!target || !calculation_item_id) return '[적용항목, 적용공식]을 모두 선택하세요.';
	if (!from_dt) return '[적용 시작일시]를 선택하세요.';
	
	const param = [];			
	for (const ch of ['a','b','c','d','e']) {
	    const $paramEl = $(`#param-${ch}-val`);
	    if ($paramEl.prop('disabled')) continue;

	    const val = $paramEl.val().trim();
	    if (val === '') return '[대입값]을 모두 입력하세요.';

	    param.push({
	        param_key: ch.toUpperCase(),
	        param_value: val
	    });
	}
	
	return {
		sensor_id,
		calculation_item_id,
		from_dt,
		to_dt,
		param
	};
}


/* DMS 센서정보 적용 계산식 테이블 대입값 적용 */
function setCalculationOfParam() {
	$('#apply-calculation-body tr').each(function () {
		const $tr = $(this);
	    const data = $tr.data();
	    const params = {
	        A: data.param_a,
	        B: data.param_b,
	        C: data.param_c,
	        D: data.param_d,
	        E: data.param_e
	    };

	    let formula = data.formula.replace(/\b[A-E]\b/g, match => params[match] ?? match);
	    $tr.children('td').eq(1).text(formula);
    });
}


/* DMS 센서정보 적용 항목 변경시 적용 공식 필터 */
function AppylTargetChangeFilterCalculationCombo() {
	const selectedVal = $('#sensor-target-column').val();
	const $calculation = $('#sensor-apply-calculation');

	$calculation.prop('disabled', !selectedVal);

	$calculation.find('option').each(function () {
		$(this).toggle($(this).data('target_column') === selectedVal);
	});
}


/* DMS 센서정보 적용 계산식 변경시 입력폼 재설정 */
function ApplyCalculationChangeCombo(el) {
	const value = $(el).val();
	$('#final-calculation').val(value);

	['a','b','c','d','e'].forEach(ch => {
		$(`#param-${ch}-val`).prop('disabled', !value.includes(ch.toUpperCase())).val('');
	});
	
	['#sensor-calculation-start', '#sensor-calculation-end'].forEach((selector, i) => {
		$(selector).prop('disabled', false).val(i === 0 ? getKSTFormattedDateTime() : '');
	});
}


/* DMS 센서정보 적용 계산식 파람 입력값 계산식에 대입 */
function ParamOnInputChangeCalculation(el) {
	el.value = el.value
        .replace(/[^0-9.\-]/g, '')
        .replace(/(\..*?)\..*/g, '$1')
        .replace(/(?!^)-/g, '');

    if (el.value === '.') el.value = '0.';
    if (el.value === '-.') el.value = '-0.';

    if (el.value.startsWith('.')) el.value = `0${el.value}`;
    if (el.value.startsWith('-.')) el.value = `-0.${el.value.slice(2)}`;

    const values = {};
    $('#param-a-val, #param-b-val, #param-c-val, #param-d-val, #param-e-val').each(function () {
        const ch = this.id.split('-')[1].toUpperCase();
        values[ch] = this.value.trim();
    });

    let calculation = $('#sensor-apply-calculation').val();

    for (const [ch, val] of Object.entries(values)) {
        calculation = val ? calculation.replaceAll(ch, `(${val})`) : calculation;
    }

    $('#final-calculation').val(calculation);
}


/* DMS 센서정보 적용 계산식 조회 데이터 테이블 필터 */
function ApplyCalculationChangeFilter(el) {
	const value = $(el).val();
	$('#apply-calculation-body tr').each(function () {
		$(this).toggle(!value || $(this).data('target_column') === value);
	});
}


/* DMS 센서정보 적용 초기치 저장 */
async function InsertDmsSensorInitial() {
	const result = await InsertDmsSensorInitialFormValidation();
	if (typeof result === 'string') return showMessage(result);
	
	try {
		const res = await PostRequest('sensorinitial', result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
					
		const comboBody = {
			category: $('#category').val(),
			placeId: $('#sensor-place-id').val(),
			sensorId: $('#sensor-id').val()
		};
		
		const $html = await PostRequest('combo', comboBody, 'text');
		const $parsed = $('<table>' + $html + '</table>');
	    $('#sensor-initial-body').html($parsed.find('#sensor-initial-body').html());
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ResetDmsForm('sensorInitial');
	}
}


/* DMS 센서정보 적용 초기치 입력값 유효검증 + body 반환 */
async function InsertDmsSensorInitialFormValidation() {
	const comboBody = {
		category: $('#category').val(),
		placeId: $('#sensor-place-id').val(),
		sensorId: $('#sensor-id').val()
	};
	if (comboBody.placeId === '0' || comboBody.sensorId === '0')
		return '[현장]과 [센서]를 먼저 선택하세요.';

	const initial = $('#initial-sensor-val').val().trim();
	const from_dt = $('#sensor-initial-start').val();
	const toDtVal = $('#sensor-initial-end').val().replace(' ', 'T');
	const to_dt = toDtVal === '' ? null : toDtVal;
	
	if (!initial || !from_dt)
		return '[초기 측정값]과 [적용 시작일시]를 입력하세요.';

	const $html = await PostRequest('combo', comboBody, 'text');
	const $parsed = $('<table>' + $html + '</table>');
	$('#apply-calculation-body').html($parsed.find('#apply-calculation-body').html());
	setCalculationOfParam();

	const applyObj = $('#apply-calculation-body').find('tr').map((_, tr) => {
		const $td = $(tr).find('td');
		const toDtText = $td.eq(3).text().trim();
		return {
			target_column: $td.eq(0).text().trim(),
			calculation:   $td.eq(1).text().trim(),
			from_dt:       $td.eq(2).text().trim(),
			to_dt:         toDtText === '무기한' ? null : toDtText,
			id:       	   $td.eq(4).text().trim(),
		};
	}).get();

	if (applyObj.length === 0)
		return '[센서 적용 계산식]을 먼저 등록하세요.';

	const toDate = s => new Date(s.replace(' ', 'T'));
	const requiredTargets = ['correction', 'displace'];
	
	if (applyObj.some(o => o.calculation.includes('angle'))) {
		requiredTargets.push('angle');
	}

	const activeRecords = applyObj.filter(o => {
		const from = toDate(o.from_dt);
		const to   = o.to_dt ? toDate(o.to_dt) : null;
		const target = toDate(from_dt);
		return target >= from && (!to || target <= to);
	});

	for (const target of requiredTargets) {
		const existsAll   = applyObj.some(o => o.target_column.includes(target));
		const existsActive = activeRecords.some(o => o.target_column.includes(target));
		if (!existsAll) {
			return `[적용 항목: ${target}] 계산식을 먼저 등록하세요.`;
		}
		if (!existsActive) {
			return `입력한 초기값 [적용 시작일시]가 [${target} 계산식]의 적용 \n기간 범위에 포함되지 않습니다.\n\n` +
				   '※ 계산식의 적용 기간을 확인해주세요.';
		}
	}

	const isFromDtValid = activeRecords.some(item => {
	    return toDate(from_dt) >= toDate(item.from_dt);
	});
	if (!isFromDtValid)
		return '[초기값 적용 시작일시]는 [계산식 적용 시작일시]와 같거나 이후여야 합니다.';

	const latestRecords = {};
	for (const keyword of requiredTargets) {
		const filtered = activeRecords.filter(o => o.target_column === keyword);
		if (filtered.length === 0) continue;
		const latest = filtered.reduce((a, b) => (a.id > b.id ? a : b));
		latestRecords[keyword] = latest;
	}

	const result = calculateValues(latestRecords, initial);
	if (typeof result === 'boolean') 
		return showMessage('계산 중 오류가 발생했습니다. 수식을 확인하세요.');
	
	return {
		sensor_id: comboBody.sensorId,
		raw_val: result.initial,
		displace_val: result.displace,
		from_dt,
		to_dt
	};
}


/* DMS 센서정보 초기치 계산식 적용 계산 */
function evaluateFormula(formula, vars) {
	try {
		const mathContext = {
			sin: Math.sin,
			cos: Math.cos,
			tan: Math.tan,
			asin: Math.asin,
			acos: Math.acos,
			atan: Math.atan,
			pi: Math.PI,
			PI: Math.PI
		};
		const context = { ...mathContext, ...vars };
		const keys = Object.keys(context);
		const values = Object.values(context);
		const func = new Function(...keys, `return ${formula};`);
		const result = func(...values);
		return Number(result.toFixed(3));
	} catch (e) {
		console.error('수식 계산 오류:', formula, e);
		return false;
	}
}


/* DMS 센서정보 초기치 계산식 적용 중간 계산 결과 */
function calculateValues(latestRecords, initial) {
	const vars = {};
	const baseVal = Number(Number(initial).toFixed(3));
	
	vars.raw = baseVal;
	vars.initial = baseVal;
	vars.correction = baseVal;
		
	if (latestRecords.angle) {
		const formula = latestRecords.angle.calculation;
		const angleVal = evaluateFormula(formula, vars);
		if (angleVal === false) return false;
		vars.angle = angleVal;
	}
	
	if (latestRecords.displace) {
		const formula = latestRecords.displace.calculation;
		const displaceVal = evaluateFormula(formula, vars);
		if (displaceVal === false) return false;
		vars.displace = displaceVal;
	}
	
	return vars;
}


/* DMS 센서타입정보 저장,수정,삭제 요청 */
async function UpsertDmsSensorTypeInfoForm(mode) {
	const FORM_TYPE = 'sensortype';
	const result = DmsSensorTypeFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);
	
	if (!mode) {
		if (result.id === '0') return ResetDmsForm(FORM_TYPE);
		if (!confirm(`[${result.name}]의 정보를 삭제하겠습니까?`)) return;
	}
	
	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ComboChangePostMethod();
		ResetDmsForm('sensorType');
	}
}


/* DMS 센서타입정보 입력값 유효검증 + body 반환 */
function DmsSensorTypeFormValidation(mode) {
	const id = $('#sensor-type-id').val();
	const code = $('#sensor-type-code').val().trim();
	const name = $('#sensor-type-name').val().trim();
	const duplicateCheck = $('#checking-sensor-type-code').text();
	
	if (!code || !name) return '*필수 정보* 모두 입력해 주세요.';
	if (duplicateCheck !== '사용가능') return '[센서타입코드] 중복확인 후 저장하세요';
		
	return {
		id,
		code,
		name,
		mode
	};
}


/* DMS 센서타입정보 내의 센서타입 리스트 클릭 */
function SelectedSensorTypeLi(target) {
	selectListItem($('#sensor-type-ul'), target, 'li');
	
	const { id, code, name } = target.dataset;
	
	$('#sensor-type-id').val(id);
	$('#sensor-type-code').val(code).prop('disabled', true);
	$('#sensor-type-name').val(name);
	setButtonState($('#checking-sensor-type-code'), '사용가능', 'btn-outline-primary');
}


/* DMS 센서타입정보 현장적용 센서타입설정 저장 요청 */
async function UpsertDmsSensorTypePlaceSensorTypeInfoForm() {
	const FORM_TYPE = 'sensortypesetting';
	const result = DmsSensorTypePlaceSensorTypeFormValidation();
	if (typeof result === 'string') return showMessage(result);
	
	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ResetDmsForm('sensorTypeSetting');
	}
}


/* DMS 센서타입정보 현장적용 센서타입설정 입력값 유효검증 + body 반환 */
function DmsSensorTypePlaceSensorTypeFormValidation() {
	const id = $('#sensor-type-setting-id').val();
	const group_order = $('#sensor-type-setting-group-order').val().trim();
	const min_gauge = $('#sensor-type-setting-min-gauge').val().trim();
	const max_gauge = $('#sensor-type-setting-max-gauge').val().trim();

	if (id === '0') return '[센서타입]을 먼저 선택하세요.';
	if (!group_order || !min_gauge || !max_gauge) return '*필수 정보* 모두 입력해 주세요.';
	
	return {
		id,
		group_order,
		min_gauge,
		max_gauge,
		criteria_val1: $('#sensor-type-setting-criteria-val1').val()?.trim() || null,
		criteria_val2: $('#sensor-type-setting-criteria-val2').val()?.trim() || null,
		criteria_val3: $('#sensor-type-setting-criteria-val3').val()?.trim() || null,
		interval: $('#sensor-type-setting-interval').val()?.trim() || '0',
		hidden: $('#sensor-type-setting-hidden').val()
	};
}


/* 센서타입정보 내의 현장 리스트 클릭 이벤트 및 요청 */
async function SelectedSensorTypePlaceLi(target) {
	selectListItem($('#sensor-type-place-ul'), target, 'li');

	const category = $('#category').val();
	const placeId = target.dataset.id;
	$('#sensor-type-setting-place-id').val(placeId);

	try {
		const $html = await PostRequest("combo", { category, placeId }, "text");
        const $parsed = $('<div>').html($html);
		const sensorTypeSettingUl = $parsed.find('#sensor-type-setting-ul').html();
		$('#sensor-type-setting-ul').html(sensorTypeSettingUl);
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* DMS 센서타입정보의 선택 현장내의 센서타입 리스트 클릭 */
function SelectedSensorTypePlaceSensorTypeLi(target) {
	selectListItem($('#sensor-type-setting-ul'), target, 'li');
	
	const ds = target.dataset;
	const d = {
		id: ds.id,
		name: ds.name,
		group_order: ds.group_order,
		min_gauge: ds.min_gauge,
		max_gauge: ds.max_gauge,
		criteria_val1: ds.criteria_val1 || '',
		criteria_val2: ds.criteria_val2 || '',
		criteria_val3: ds.criteria_val3 || '',
		interval: ds.interval,
		hidden: ds.hidden === 'true'? '1' : '0'
	};
	
	$('#sensor-type-setting-id').val(d.id);
	$('#sensor-type-setting-name').val(d.name);
	$('#sensor-type-setting-group-order').val(d.group_order);
	$('#sensor-type-setting-min-gauge').val(d.min_gauge);
	$('#sensor-type-setting-max-gauge').val(d.max_gauge);
	$('#sensor-type-setting-criteria-val1').val(d.criteria_val1);
	$('#sensor-type-setting-criteria-val2').val(d.criteria_val2);
	$('#sensor-type-setting-criteria-val3').val(d.criteria_val3);
	$('#sensor-type-setting-interval').val(d.interval);
	$('#sensor-type-setting-hidden').val(d.hidden);
}


/* DMS 센서타입정보의 선택 현장내의 센서타입 리스트 초기화 */
function UpdateElement(val, $elements) {
	$elements.each(function () {
		const $el = $(this);
		if ($el.data('id') == val) {
			SelectedSensorTypePlaceLi(this);
		}
	});
}


/* DMS 계산식정보 저장,수정,삭제 요청 */
async function UpsertDmsCalculationInfoForm(mode) {
	const FORM_TYPE = 'calculation';
	const result = DmsCalculationFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);

	if (!mode) {
		if (result.id === '0') return ResetDmsForm(FORM_TYPE);
		if (!confirm(`[${result.name}]의 정보를 삭제하겠습니까?`)) return;
	}
	
	try {
		const res = await PostRequest(FORM_TYPE, result);
		showMessage(res.header?.message || "처리에 실패했습니다.");
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	} finally {
		ComboChangePostMethod();
		ResetDmsForm(FORM_TYPE);
	}
}


/* DMS 계산식정보 입력값 유효검증 + body 반환 */
function DmsCalculationFormValidation(mode) {
	const msg = '계산식 오류 [계산공식]을 확인해주세요.';
	const id = $('#calculation-id').val();
	const name = $('#calculation-name').val().trim();
	const target_column = $('#target-column').val();
	const formula = $('#calculation-formula').val();
	const duplicateCheck = $('#checking-calculation-name').text();
	
	if (!name || !target_column || !formula) return '*필수 정보* 모두 입력해 주세요.';
	
	try {
		if (/(\+\+|--)/.test(formula)) return msg;
		
		let valid = true;
		const node = math.parse(formula);
		
		node.traverse(function (node, path, parent) {
	        if (node.isSymbolNode) {
	            if (!allowed.includes(node.name)) valid = false;
	        }
			if (node.isOperatorNode) {
	            if (!allowedOperators.includes(node.op)) valid = false;
	        }
	        if (node.isFunctionNode) valid = false;
	    });
		if (!valid) return msg;
	} catch (err) {
		return msg;
	}
	
	if (duplicateCheck !== '사용가능') return '[계산식명칭] 중복확인 후 저장하세요';
	
	return {
		id,
		name,
		target_column,
		formula,
		description: $('#calculation-description').val().trim(),
		mode
	};
}


/* DMS 계산식정보 내의 계산식 리스트 클릭 */
function SelectedCalculationLi(target) {
	selectListItem($('#calculation-ul'), target, 'li');
	
	const ds = target.dataset;			
	const d = {
		id: ds.id,
		name: ds.name,
		target_column: ds.target_column,
		formula: ds.formula,
		description: ds.description || ''
	};
	
	$('#calculation-id').val(d.id);
	$('#calculation-name').val(d.name).prop('disabled', true);
	$('#target-column').val(d.target_column).prop('disabled', true);
	$('#calculation-description').val(d.description);
	$('#calculation-formula').val(d.formula);
	
	setButtonState($('#checking-calculation-name'), '사용가능', 'btn-outline-primary');
	$('.cal-btn, .cal-big-btn').addClass('disabled');
}


/* DMS 계산식정보 공식 입력 버튼 활성화 여부 적용 */
function FilterCalculationButton() {
	formulaValues.length = 0;
	$('#calculation-formula').val('');
	$('.cal-btn, .cal-big-btn').removeClass('disabled');
	
	const disableMap = {
		correction: ['#btn-correction', '#btn-angle', '#btn-displace'],
		angle: ['#btn-angle', '#btn-displace'],
		displace: ['#btn-displace'],
		default: ['.cal-btn', '.cal-big-btn']
	};
	
	const value = $('#target-column').val();
	const toDisable = disableMap[value] || disableMap.default;
	
	$(toDisable.join(',')).addClass('disabled');
}


/* DMS 계산식정보 계산식 입력 버튼 이벤트 */
function AddCalculationButtonEvent(target) {
	const value = $(target).data('value');
	let text = $('#calculation-formula').val();
	if (value === 'back') {
		if (formulaValues.length) {
			formulaValues.pop();
			text = formulaValues.join('');
		}
	} else {
		formulaValues.push(value);
		text += value;
	}
	$('#calculation-formula').val(text);
}


/* DMS 공용 정보 입력폼 초기화 */
function ResetDmsForm(title, selector = false) {
	switch (title) {
		case 'computer':
			$('#computer-id').val('0');
			$('#computer-name').val('').prop('disabled', false);
			$('#computer-ip').val('');
			$('#computer-description').val('');
			$('#computer-ul li').removeClass('row-select');
			setButtonState($('#checking-computer-code'), '중복확인', 'btn-outline-warning');
			break;
		case 'company':
			$('#company-id').val('0');
			$('#company-full-name').val('').prop('disabled', false);
			$('#company-short-name').val('');
			$('#company-tel').val('');
			$('#company-address').val('');
			$('#company-ul li').removeClass('row-select');
			setButtonState($('#checking-company-code'), '중복확인', 'btn-outline-warning');
			break;
		case 'place':
			$('#place-id').val('0');
			$('#place-code').val('').prop('disabled', false);
			$('#place-short-name').val('');
			$('#place-full-name').val('');
			$('#place-manager').val('');
			$('#place-url').val('');
			$('#place-user').val('');
			$('#place-password').val('');
			$('#place-run').prop('checked', false);
			$('#place-open').prop('checked', false);
			$('#place-company-id').val('2');
			$('#place-uid').val('0');
			$('#place-ul li').removeClass('row-select');
			setButtonState($('#checking-place-code'), '중복확인', 'btn-outline-warning');
			break;
		case 'logger':
			$('#logger-id').val('0');
			$('#logger-code').val('').prop('disabled', false);
			$('#logger-name').val('');
			$('#logger-location').val('');
			$('#logger-cdma-no').val('');
			$('#logger-file-path').val('');
			$('#logger-computer-id').val('1');
			$('#logger-cycle-check').val('1');
			$('#logger-run').prop('checked', false);
			$('#logger-install-dt').val(getTodayDate());
			$('#logger-ul li').removeClass('row-select');
			setButtonState($('#checking-logger-code'), '중복확인', 'btn-outline-warning');
			if (selector) {
				$('#logger-place-id').val('0');
				$('#logger-place-ul li').removeClass('row-select');
				$('#logger-ul').html('<li>현장을 선택하세요</li>');
			}
			break;
		case 'sensor':
			$('#sensor-id').val('0');
			$('#sensor-code').val('');
			$('#sensor-name').val('');
			$('#sensor-location').val('');
			$('#sensor-install-dt').val(getKSTFormattedDateTime);
			$('#sensor-sensor-type').val('');
			$('#sensor-order').val('');
			$('#sensor-ul li').removeClass('row-select');
			setButtonState($('#checking-sensor-code'), '중복확인', 'btn-outline-warning');
			ResetDmsForm('applyCalculation', true);
			ResetDmsForm('sensorInitial', true);
			if (selector) {
				$('#sensor-place-id').val('0');
				$('#sensor-place-code').val('');
				$('#sensor-logger-id').val('0');
				$('#sensor-logger-code').val('');
				$('#sensor-logger-combo').html('<option selected disabled>현장을 선택하세요</option>');
				$('#sensor-place-ul li').removeClass('row-select');
				$('#sensor-ul').empty();
			}
			break;
		case 'sensorType':
			$('#sensor-type-id').val('0');
			$('#sensor-type-code').val('').prop('disabled', false);
			$('#sensor-type-name').val('');
			$('#sensor-type-ul li').removeClass('row-select');
			setButtonState($('#checking-sensor-type-code'), '중복확인', 'btn-outline-warning');
			break;
		case 'sensorTypeSetting':
			const placeId = $('#sensor-type-setting-place-id').val();
			$('#sensor-type-setting-id').val('0');
			$('#sensor-type-setting-place-id').val('0');
			$('#sensor-type-setting-name').val('');
			$('#sensor-type-setting-group-order').val('');
			$('#sensor-type-setting-min-gauge').val('');
			$('#sensor-type-setting-max-gauge').val('');
			$('#sensor-type-setting-criteria-val1').val('');
			$('#sensor-type-setting-criteria-val2').val('');
			$('#sensor-type-setting-criteria-val3').val('');
			$('#sensor-type-setting-interval').val('');
			$('#sensor-type-setting-hidden').val('0')
			$('#sensor-type-setting-ul li').removeClass('row-select');
			if (selector) {
				$('#sensor-type-setting-ul').html('<li>현장을 선택하세요</li>');
				$('#sensor-type-place-ul li').removeClass('row-select');
			} else {
				UpdateElement(placeId, $('#sensor-type-place-ul li'));
			}
			break;
		case 'calculation':
			$('#calculation-id').val('0');
			$('#calculation-name').val('').prop('disabled', false);
			$('#target-column').prop('disabled', false).val('').change();
			$('#calculation-description').val('');
			$('#calculation-formula').val('');
			setButtonState($('#checking-calculation-name'), '중복확인', 'btn-outline-warning');
			formulaValues.length = 0;
			break;
		case 'applyCalculation':
			$('#sensor-target-column').val('');
			$('#sensor-apply-calculation').val('').prop('disabled', true);
			$('#final-calculation').val('');
			['a','b','c','d','e'].forEach(ch => {
				const $paramEl = $(`#param-${ch}-val`);
				$paramEl.prop('disabled', true).val('');
			});
			$('#sensor-calculation-start').val('').prop('disabled', true);
			$('#sensor-calculation-end').val('').prop('disabled', true);
			if (selector) {
				$('#apply-calculation-body').empty();
				$('#sensor-target-column').prop('disabled', true);
			} else {
				$('#sensor-target-column').prop('disabled', false);
			}
			break;
		case 'sensorInitial':
			$('#initial-sensor-val').val('');
			$('#sensor-initial-start').val('');
			$('#sensor-initial-end').val('');
			if (selector) {
				$('#initial-sensor-code').val('');
				$('#sensor-initial-body').empty();
			}
			break;
	}
}


/******** 파일관리 > 이동 및 백업 *******************************************************************************************/

/* 파일관리 이동및백업 저장,수정,삭제 요청 */
async function UpsertMoveAndBackInfo(mode) {
	if(!LevelValidation()) return showMessage('현재 계정으로는 해당 기능을 사용할 수 없습니다.');
	
	const result = MoveAndBackFormValidation(mode);
	if (typeof result === 'string') return showMessage(result);
	if (typeof result === 'boolean') return;
	
	try {
		const res = await PostRequest('filemove', result);

		if (res.header?.messageCd === 200) {
			showMessage(res.header.message);
			loadContent($('#category').val());
		} else {
			showMessage(res.header.message);
		}
	} catch (e) {
		console.error(e);
		showMessage("서버와의 연결에 문제가 발생했습니다. (네트워크 오류)");
	}
}


/* 파일관리 이동및백업 입력값 유효검증 + body 반환 */
function MoveAndBackFormValidation(mode) {
    const id = $('#appendId').val();
	const code = $('#appendCd').val();
	
	if (!mode) {
		if (id === '0') return false;
		if (confirm(`[${code}]의 정보를 삭제하겠습니까?`)) return {id, mode};
		return false;
	}
	
	const allowedExt = ['txt', 'dat'];
    const getFileName = (selector, fallback) => {
        const files = $(selector)[0].files;
        return files.length > 0 ? files[0].name : fallback;
    };
    const getExt = (filename) => filename.split('.').pop().toLowerCase();
    const isValidExt = (filename) => allowedExt.includes(getExt(filename));

    const name = $('#name').val().trim();
    const pc_id = $('#pc_id').val();
    const parsing_type_id = $('#parsing_type_id').val();
    const run = $('#run').prop('checked');

    const source_name = $('#source_name').val();
    const target_name = $('#target_name').val();
    const source_path = $('#source_path').val().trim();
    const target_path = $('#target_path').val().trim();

    const source_file = getFileName('#source_file', source_name);
    const target_file = getFileName('#target_file', target_name);

    if (!name || !source_path || !target_path) return '정보를 모두 입력하세요.';
    if (!pc_id) return '[수집 PC]를 선택하세요.';
    if (!parsing_type_id) return '[파싱 타입]을 선택하세요.';
    if (!target_name && !target_file) return '[이동 파일]을 선택하세요. (txt, bat 파일)';
    if (!isValidExt(target_file || target_name)) return '[이동 파일] 형식을 확인하세요. (txt, bat 파일)';
    if (parsing_type_id === '8') {
        if (!source_name && !source_file) return '[수집 파일]을 선택하세요. (txt, bat 파일)';
        if (!isValidExt(source_file || source_name)) return '[수집 파일] 형식을 확인하세요. (txt, bat 파일)';
    }
    if (id !== '0') {
        if (!confirm(`[${name}]의 정보를 변경하겠습니까?\n\n※신규 추가는 초기화 버튼 클릭 후 진행하세요.`))
            return false;
    }
	
	return {
        id,
        name,
        pc_id,
        source_name: source_file || source_name,
        source_path,
        target_name: target_file || target_name,
        target_path,
        run,
        parsing_type_id,
        mode
    };
}


/* 파일관리 이동및백업 테이블 행 클릭 */
function SelectedMoveAndBackRow(target) {
	selectListItem($('#collect-body'), target, 'tr');

	const ds = target.dataset;
	const d = {
		id: ds.id,
		name: ds.name,
		pc_id: ds.pc_id,
		source_name: ds.source_name || '',
		source_path: ds.source_path,
		target_name: ds.target_name || '',
		target_path: ds.target_path,
		run: ds.run === 'true',
		parsing_type_id: ds.parsing_type_id
	};
	
	$('#appendId').val(d.id);
	$('#appendCd').val(d.name);
	$('#name').val(d.name);
	$('#run').prop('checked', d.run);
	$('#pc_id').val(d.pc_id);
	$('#source_name').val(d.source_name);
	$('#source_path').val(d.source_path);
	$('#target_name').val(d.target_name);
	$('#target_path').val(d.target_path);
	$('#parsing_type_id').val(d.parsing_type_id);
	$('#source_file').val('');
	$('#target_file').val('');
}


/* 파일관리 공용 입력폼 초기화 */
function ResetFileManageForm(title) {
	switch (title) {
		case 'moveAndBack':
			$('#appendId').val('0');
			$('#appendCd').val('');
			$('#name').val('');
			$('#pc_id').val('');
			$('#source_name').val('');
			$('#source_file').val('');
			$('#source_path').val('');
			$('#target_name').val('');
			$('#target_file').val('');
			$('#target_path').val('');
			$('#parsing_type_id').val('');
			$('#run').prop('checked', false);
			$('#collect-body').find('tr').removeClass('row-select');
			break;
	}
}


/******** 화면 로딩시 실행 *******************************************************************************************/

/* 화면 시작시 실행 이벤트 */
$(document).ready(function () {
	
	/* 드랍 메뉴 클릭 이벤트 */
	$('[data-category]').on('click', function (e) {
		e.preventDefault();
		loadContent($(this).data('category'), this);
	});
	
	/* 카테고리 컨텐츠 */
	loadContent($('#category').val());
});
