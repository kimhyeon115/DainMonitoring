
/* 상태 변수 */
let Status = true;

$(document).ready(async function () {
	
	$('#loadingOverlay').show();
    highlightOldRows();

    async function getLatestData() {
        const placeCode = $('body').data('page-code');
        try {
            const response = await fetch(`/${placeCode}/api/main`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
				redirect: 'manual'
            });
            if (!response.ok) return;
			if (Status) {
	            const result = await response.json();
	            replaceLatestData(result);
			} else {
				location.reload(true);
			}
			Status = true;
        } catch (error) {
            console.error(error);
			Status = false;
			return;
        }
    }

    await getLatestData();
    setInterval(getLatestData, 10 * 60 * 1000);
	$('#loadingOverlay').hide();
});



/** 24시간 이상 데이터 없을 시 배경색 변경 */
function highlightOldRows() {
    $("#loggers_body tr").each(function () {
        const $row = $(this);
        const $finalDateTd = $row.find(".last-at");
        if ($finalDateTd.length === 0) return;

        const finalDateStr = $finalDateTd.text().trim();
        const finalDate = new Date(finalDateStr.replace(' ', 'T'));
        const now = new Date();

        if (!isNaN(finalDate)) {
            const hoursDiff = (now - finalDate) / (1000 * 60 * 60);
            if (hoursDiff >= 24) {
                $row.css("background-color", "#FFE28B");
            }
        }
    });
}



function replaceLatestData(dataObj) {
	updateLoggerTable('.logger-tr', dataObj.loggers);
    updateSensorTable('.sensor-hour-tr', dataObj.latestCycleData, 
		['initialVal', 'displaceVal', 'changedVal', 'cumulativeVal']
	);
    updateSensorTable('.sensor-event-tr', dataObj.latestEventData, 
		['lastMeasuredAt', 'displaceVal', 'maxDisplaceVal', 'todayDataCount']
	);
	updateSensorTableTd('.sensor-hour-td', dataObj.latestCycleData, 'changedVal');

    renderGraphs('.chart-cycle-type', dataObj.cycleChartData, dataObj.graphSet, 'line');
    renderGraphs('.chart-event-type', dataObj.eventChartData, dataObj.graphSet, 'column');
	renderLastedGraphs('.chart-lasted-type', dataObj.graphSet);
	renderRailGraphs('.chart-rail-type',dataObj.graphSet);
}



/** 로거 현황 테이블 값 업데이트 **/
function updateLoggerTable(selector, dataMap) {
	$(selector).each((idx, el) => {
		$(el).find('.last-at').text(dataMap[idx].lastAt);
	});
}



/** 공통 센서 테이블 값 업데이트 */
function updateSensorTable(selector, dataMap, fields) {
    $(selector).each((_, el) => {
        const $el = $(el);
        const loggerId = $el.data('logger');
        const sensorCode = $el.data('code');
		const sensorTypeId = $el.data('type');
        const data = dataMap[loggerId]?.flat() || [];
        const row = data.find(obj => obj.sensorCode === sensorCode);
        if (!row) return;

		fields.forEach(field => {
		    let value = row[field];
		    if (sensorTypeId == 12) {
		        value = parseInt(value, 10);
		    } else if (sensorTypeId == 5 || sensorTypeId == 6 || sensorTypeId == 14) {
				value = parseFloat(value).toFixed(1);
			}
		    $el.find(`.${toKebabCase(field)}`).text(value);
		});
    });
}



/** 도상침하계 센서 테이블 값 업데이트 */
function updateSensorTableTd(selector, dataMap, field) {
	$(selector).each((_, el) => {
        const $el = $(el);
        const loggerId = $el.data('logger');
        const sensorCode = $el.data('code');
        const data = dataMap[loggerId]?.flat() || [];
        const row = data.find(obj => obj.sensorCode === sensorCode);

        if (!row) return;
	    $el.text(row[field]);
    });
}



/** 그래프 렌더링 공통 처리 */
function renderGraphs(selector, graphDataMap, graphSet, chartType) {
    $(selector).each((_, el) => {
        const $el = $(el);
        const chartId = el.id;
        const dataKey = chartId.replace('graph_', '');
        const rows = graphDataMap[dataKey];
        const sensorTypeId = $el.data('type');
        const title = chartType == 'line' ? '최근 변화 그래프' : '24시간 변화 그래프';

        const rangeSet = graphSet.find(obj => obj.sensorTypeId === sensorTypeId) || {};
        const min = parseFloat(rangeSet.minGauge);
        const max = parseFloat(rangeSet.maxGauge);

        if (!rows || rows.length <= 1) {
            const emptyData = generateEmptyDateRows(chartType);
            const column = [
                { type: 'date', label: '측정일시' },
                { type: 'number', label: '데이터' }
            ];

			chartType == 'line' ?
				lineChart(title, min, max, column, emptyData, chartId)
				:columnChart(title, min, max, column, emptyData, chartId);
            return;
        }

        const columnNames = rows[0];
        const dataRows = rows.slice(1).map(row => [new Date(row[0]), ...row.slice(1)]);
        const column = columnNames.map((name, i) => ({
			type: i === 0 ? 'datetime' : 'number',
            label: name
        }));
        const options = {};
        for (let i = 1; i < columnNames.length; i++) {
            options[i - 1] = { pointsVisible: true, pointSize: 3, lineWidth: 2 };
        }

		chartType == 'line' ?
			lineChart(title, min, max, column, dataRows, chartId)
			:columnChart(title, min, max, column, dataRows, chartId);
    });
}



/** 수직 그래프 랜더링 처리 */
function renderLastedGraphs(selector, graphSet) {
	$(selector).each((_, el) => {
		const $el = $(el);
        const chartId = el.id;
        const sensorTypeId = $el.data('type');
        const title = '지중경사계 변화 그래프';
		
		const rangeSet = graphSet.find(obj => obj.sensorTypeId === sensorTypeId) || {};
        const min = parseFloat(rangeSet.minGauge);
        const max = parseFloat(rangeSet.maxGauge);
		$el.css('height', '540px');
		
		const rows = $el.parent().siblings('div').find('tbody tr').toArray();
		const data = rows.map(tr => {
		    const $tr = $(tr);
			const location = Number($tr.find('.location').text().trim());
		    const cumulativeVal = Number($tr.find('.cumulative-val').text().trim());
		    return [location, cumulativeVal];
		});
		
		lineChartOfIPI(title, min, max, data, chartId);
	});
};



/** 레일 그래프 랜더링 처리 */
function renderRailGraphs(selector, graphSet) {
	$(selector).each((_, el) => {
		const $el = $(el);
        const chartId = el.id;
        const sensorTypeId = $el.data('type');
        const title = '도상침하계 변화 그래프';
		const column = [
			{type: 'string'},
			{type: 'number', label: '변화량(mm)'}
		];
		
		const rangeSet = graphSet.find(obj => obj.sensorTypeId === sensorTypeId) || {};
        const min = parseFloat(rangeSet.minGauge);
        const max = parseFloat(rangeSet.maxGauge);

		const dataRows = [];
		$el.parent().siblings('div').find('div > table tr').each(function() {
		    const $tds = $(this).find('td');

		    for (let i = 0; i < $tds.length; i += 2) {
		        const code = $tds.eq(i).text().trim();
		        const value = Number($tds.eq(i + 1).text().trim());
		        
				if (code && !isNaN(value)) {
                    dataRows.push([code, value]);
                }
		    }
		});
		
		lineChart(title, min, max, column, dataRows, chartId);
	});
}



/** 빈 날짜 데이터 생성 (그래프용) */
function generateEmptyDateRows(type = 'line') {
    const today = new Date();
    const dates = [];
    const step = type === 'line' ? 1 : 4;  // 일단위 vs 4시간 단위
    const count = type === 'line' ? 7 : 7;

    for (let i = count - 1; i >= 0; i--) {
        const d = new Date(today);
        type === 'line' ? d.setDate(d.getDate() - i) : d.setHours(d.getHours() - i * step);
        dates.push([d, null]);
    }
    return dates;
}



/** camelCase → kebab-case 변환 */
function toKebabCase(str) {
    return str.replace(/[A-Z]/g, m => '-' + m.toLowerCase());
}
