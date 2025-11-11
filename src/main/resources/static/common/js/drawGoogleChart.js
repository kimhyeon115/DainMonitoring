
/* 메인 화면 구글 라인 차트 */
function lineChart(title, min, max, column, row, chartId) {

    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(function() {
		
        let options = {
            width: '100%',
            height: '100%',
            legend: {
				position: "top",
				maxLines: 2,
				alignment: "end",
				textStyle: {
					fontSize: 9
				}
			},
            title: title,
            chartArea: {
				top: 50,
				bottom: 40,
				left: 50,
				right: 35
			},
            hAxis: {
                format: 'yyyy-MM-dd HH:mm',
                textStyle: {
					color: '#000',
					fontName: 'Arial',
					fontSize: 9
				},
            },
            vAxes: {
                0: {
                    viewWindow: {
						min: min,
						max: max
					},
                    textStyle: {
						color: '#000',
						fontName: 'Arial',
						fontSize: 9
					},
                    titleTextStyle: {
						color: '#000',
						fontName: 'Arial',
						italic: false,
						fontSize: 9
					},
                },
            },
            series: createSeries(column),
			dataOpacity:0.4
        };
		
        let data = new google.visualization.DataTable();		
        column.forEach(col => data.addColumn(col.type, col.label));
        data.addRows(row);

        let chart = new google.visualization.LineChart(document.getElementById(chartId));
        chart.draw(data, options);
    });
}


/* 메인 화면 구글 라인 차트 (지중경사계) */
function lineChartOfIPI(title, min, max, row, chartId) {

	google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(function() {
		
        let options = {
            legend: 'none',
            title: title,
			chartArea: {
				top: 50,
				bottom: 60,
				left: 60,
				right: 50
			},
			orientation:'vertical',
            hAxis: {
				title: '변화량(mm)',
                viewWindow: {min: min, max: max}
            },
            vAxis: {
				title: '심도(M)',
				viewWindow:{
					min: row[row.length - 1].location, 
					max: row[0].location 
				}
            },
            series: {
				0:{pointsVisible: true, pointSize: 8, lineWidth:2}
			},
			dataOpacity:0.4
        };

        let data = new google.visualization.DataTable();		
		data.addColumn('number','심도(M)');
		data.addColumn('number','변화량(mm)');
        data.addRows(row);

        let chart = new google.visualization.LineChart(document.getElementById(chartId));
        chart.draw(data, options);
    });
}


/* 데이터 화면 구글 라인 차트 */
function oneSensorLineChart(min, max, column, row, chartId, senserTypeId) {
	
	if (row.length == 0) return noDataGraph(chartId);
	
	const searchStartDate = row[0][0];
	const searchEndDate = row[row.length - 1][0];
	const title = senserTypeId == 15? '수위(m)' : '변화량 (mm)';
	$('#chart').css('height', '400px');
	
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(function() {
		
        let options = {
			title: '줌: 드래그 | 복원: 우클릭',
			titleTextStyle: {
				fontName: 'Arial',
		        fontSize: 12,
		        color: '#000'
		    },
            width: '100%',
            height: '100%',
            legend: {
				position: "top",
				maxLines: 2,
				alignment: "end",
				textStyle: {
					fontSize: 9
				}
			},
            chartArea: {
				top: 50,
				bottom: 50,
				left: 70,
				right: 35
			},
			explorer: {
				axis: 'both',
				maxZoomOut: 100,
				maxZoomIn: 1000,
				keepInBounds: true,
				actions: ['dragToZoom', 'rightClickToReset'],
			},
            hAxis: {
                format: 'yyyy-MM-dd',
                textStyle: {
					color: '#000',
					fontName: 'Arial',
					fontSize: 10
				},
				maxTextLines:1,
				viewWindow: {
					min: searchStartDate,
					max: searchEndDate
				}
            },
            vAxes: {
                0: {
					title: title,
                    viewWindow: {
						min: min,
						max: max
					},
                    textStyle: { 
						color: '#000',
						fontName: 'Arial',
						fontSize: 10 
					},
                    titleTextStyle: { 
						color: '#000', 
						fontName: 'Arial', 
						italic: false, 
						fontSize: 10
					},
                },
            },
            series: {
				0: {color: '#4C3BCF', lineWidth: 2},
				1: {type: 'line', color: '#54E346', lineWidth: 1, lineDashStyle: [6, 6]},
				2: {type: 'line', color: '#54E346', lineWidth: 1, lineDashStyle: [6, 6], visibleInLegend: false},
				3: {type: 'line', color: '#FFAF00', lineWidth: 1, lineDashStyle: [6, 6]},
				4: {type: 'line', color: '#FFAF00', lineWidth: 1, lineDashStyle: [6, 6], visibleInLegend: false},
				5: {type: 'line', color: '#F93827', lineWidth: 1, lineDashStyle: [6, 6]},
				6: {type: 'line', color: '#F93827', lineWidth: 1, lineDashStyle: [6, 6], visibleInLegend: false}
			}
        };

        let data = new google.visualization.DataTable();
        column.forEach(col => data.addColumn(col.type, col.label));
        data.addRows(row);

        let chart = new google.visualization.LineChart(document.getElementById(chartId));
		chart.clearChart();
        chart.draw(data, options);
    });
}


/* 데이터 화면 구글 라인 차트 (지중경사계) */
function oneSensorLineChartOfIPI(min, max, column, row, chartId) {
	
	$('#chart').css('height', '670px');

	google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(function() {
		
        let options = {
			title: '줌: 드래그 | 복원: 우클릭',
			titleTextStyle: {
				fontName: 'Arial',
		        fontSize: 12,
		        color: '#000'
		    },
            legend: 'none',
			chartArea: {
				top: 50,
				bottom: 60,
				left: 250,
				right: 250
			},
			orientation:'vertical',
			explorer: {
				axis: 'both',
				maxZoomIn: 1000,
				keepInBounds: true,
				actions: ['dragToZoom', 'rightClickToReset']
			},
            hAxis: {
				title: '변화량(mm)',
                viewWindow: {min: min, max: max}
            },
            vAxis: {
				title: '심도(M)',
				viewWindow:{min: row[0][0], max: row[row.length - 1][0]}
            },
			dataOpacity:0.4
        };

        let data = new google.visualization.DataTable();
		
		column.forEach(col => data.addColumn("number", col));
        data.addRows(row);

        let chart = new google.visualization.LineChart(document.getElementById(chartId));
        chart.draw(data, options);
    });
}


/* 메인 화면 구글 막대 차트 */
function columnChart(title, min, max, column, row, chartId) {
	
	google.charts.load('current',{'packages':['corechart']});
    google.charts.setOnLoadCallback(function() {

        let options = {
            width:'100%',height:'100%',
            legend: { position: "none", maxLines:2, alignment:"end", textStyle: {fontSize: 9}},
            title: title,
            chartArea: {top: 50, bottom: 30, left: 50, right: 40},
            hAxis: {
                format:'yyyy-MM-dd HH:mm',
                textStyle: {color: '#000', 
                fontName: 'Arial',fontSize: 9},
            },
            vAxis: {
                title:'kine',
                viewWindow: {min: min, max: max},
                textStyle: {color: '#000', fontName: 'Arial',fontSize: 9},
                titleTextStyle: {color: '#000',fontName: 'Arial',italic:false, fontSize: 9}
            },
            series: {
                0: {color: '#4C3BCF',lineWidth: 5}
            },
            bar: {groupWidth: 5}
        };
		

		let data = new google.visualization.DataTable();
		column.forEach(col => data.addColumn(col.type, col.label));
		data.addRows(row);
		
        let chart = new google.visualization.ColumnChart(document.getElementById(chartId));
        chart.draw(data, options);
	});
}


/* 데이터 화면 구글 막대 차트 */
function oneSensorColumnChart(min, max, column, row, chartId) {
	
	const searchStartDate = row[0][0];
	const searchEndDate = row[row.length - 1][0];
	
	$('#chart').css('height', '400px');
	
	google.charts.load('current',{'packages':['corechart']});
    google.charts.setOnLoadCallback(function() {

        let options = {
			title: '줌: 드래그 | 복원: 우클릭',
			titleTextStyle: {
				fontName: 'Arial',
		        fontSize: 12,
		        color: '#000'
		    },
            width:'100%',height:'100%',
            legend: { position: "top", maxLines:2, alignment:"end", textStyle: {fontSize: 9}},
            chartArea: {top:50, bottom:50, left:70, right:35},
			explorer: {
				axis: 'both',
				maxZoomIn: 1000,
				keepInBounds: true,
				actions: ['dragToZoom', 'rightClickToReset']
			},
            hAxis: {
                format:'yyyy-MM-dd HH:mm',
                textStyle: {color: '#000', fontName: 'Arial',fontSize: 9},
				maxTextLines: 1,
				viewWindow: {
					min: searchStartDate,
					max: searchEndDate
				}
            },
            vAxis: {
                title:'kine',
                viewWindow: {min: min, max: max},
                textStyle: {color: '#000', fontName: 'Arial', fontSize: 9},
                titleTextStyle: {color: '#000', fontName: 'Arial', italic:false, fontSize: 9}
            },
			seriesType: 'bars',
            series: {
				0: {color: '#4C3BCF', lineWidth: 5},
				1: {type: 'line', color: '#54E346', lineWidth: 1, lineDashStyle: [6, 6]},
				2: {type: 'line', color: '#FFAF00', lineWidth: 1, lineDashStyle: [6, 6]},
				3: {type: 'line', color: '#F93827', lineWidth: 1, lineDashStyle: [6, 6]},
			},
			bar: {groupWidth: 5}
        };
		

		let data = new google.visualization.DataTable();
		column.forEach(col => data.addColumn(col.type, col.label));
		data.addRows(row);
		
        let chart = new google.visualization.ColumnChart(document.getElementById(chartId));
        chart.draw(data, options);
	});
}


/* 차트 옵션 가공 */
function createSeries(column) {
    return Object.fromEntries(
        column
            .map((col, i) => 
                i === 0 
                    ? null 
                    : [i - 1, { pointsVisible: true, pointSize: 4, lineWidth: 2 }]
            )
            .filter(Boolean)
    );
}


/* 그래프 데이터 DATE 타입 변환 */
function parseDate(dateString) {
	
    let year = parseInt(dateString.substring(0, 4), 10);
    let month = parseInt(dateString.substring(4, 6), 10) - 1; // 월은 0부터 시작
    let day = parseInt(dateString.substring(6, 8), 10);
    let hours = parseInt(dateString.substring(8, 10), 10);
    let minutes = parseInt(dateString.substring(10, 12), 10);
    let seconds = parseInt(dateString.substring(12, 14), 10);

    return new Date(year, month, day, hours, minutes, seconds);
}


/* 그래프 대체 이미지 */
function noDataGraph(chartId) {
	$(`#${chartId}`).show();
	$('#chart').css('height', '400px');
	let chartContainer = $(`#${chartId}`);
	chartContainer.html(`
	    <img src="/common/images/noData.png" alt="조회된 데이터가 없습니다" 
	         style="width:100%; height:100%; object-fit: contain;">
	`);
}


/* 그래프 사용 안함 */
function noGraph(chartId) {
	$(document).ready(function() {
		$(`#${chartId}`).hide();
	});
}


/* 추이 라인 차트 */
function transitionChart(column, row, min, max) {
	
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(function() {
		
        let options = {
            width: '100%',
            height: '100%',
            legend: {
				position: "top",
				maxLines: 2,
				alignment: "end",
				textStyle: {
					fontSize: 9
				}
			},
            chartArea: {
				top: 50,
				bottom: 40,
				left: 50,
				right: 35
			},
            hAxis: {
                format: 'yyyy-MM-dd HH:mm',
                textStyle: {
					color: '#000',
					fontName: 'Arial',
					fontSize: 9
				},
            },
            vAxes: {
                0: {
                    viewWindow: {
						min: min,
						max: max
					},
                    textStyle: {
						color: '#000',
						fontName: 'Arial',
						fontSize: 9
					},
                    titleTextStyle: {
						color: '#000',
						fontName: 'Arial',
						italic: false,
						fontSize: 9
					},
                },
            },
			series: {
                0: {color: '#4C3BCF',lineWidth: 3}
            },
        };
		
        let data = new google.visualization.DataTable();		
        column.forEach(col => data.addColumn(col.type, col.label));
        data.addRows(row);

        let chart = new google.visualization.LineChart(document.getElementById('chart'));
        chart.draw(data, options);
    });
}


/* 추이 막대 차트 */
function transitionColumnChart(column, row, max) {
	
	google.charts.load('current',{'packages':['corechart']});
    google.charts.setOnLoadCallback(function() {

        let options = {
            width:'100%',height:'100%',
            legend: { position: "none", maxLines:2, alignment:"end", textStyle: {fontSize: 9}},
            chartArea: {top: 50, bottom: 30, left: 50, right: 40},
            hAxis: {
                format:'yyyy-MM-dd HH:mm',
                textStyle: {color: '#000', 
                fontName: 'Arial',fontSize: 9},
            },
            vAxis: {
                title:'kine',
                viewWindow: {min: 0, max: max},
                textStyle: {color: '#000', fontName: 'Arial',fontSize: 9},
                titleTextStyle: {color: '#000',fontName: 'Arial',italic:false, fontSize: 9}
            },
			series: {
                0: {color: '#4C3BCF',lineWidth: 3}
            },
            bar: {groupWidth: 5}
        };
		

		let data = new google.visualization.DataTable();
		column.forEach(col => data.addColumn(col.type, col.label));
		data.addRows(row);
		
        let chart = new google.visualization.ColumnChart(document.getElementById('chart'));
        chart.draw(data, options);
	});
}


/* 분석 파이 차트 */
function analysisPieChart(chartData, chartId, type) {
	
	const ratio = 0.0006;
	const success = chartData[1][1];
	const fail = chartData[2][1];
	const total = fail + success;
	
	let failRatio = 0;
	if (success != 0 && fail != 0) {
		failRatio = fail / total;
		if (failRatio < ratio) {
			chartData[2][1] = total * ratio;
		}
	}
	
	google.charts.load("current", {packages:["corechart"]});
	google.charts.setOnLoadCallback(function() {

		let data = new google.visualization.arrayToDataTable(chartData);
		
		let options = {
			is3D: true,
			title: `${type} 분석 그래프`,
			tooltip: { trigger: 'none' },
			colors: ['#5B62F4', '#f76707'],
			sliceVisibilityThreshold: 0,
			chartArea: {
				left: 55, top: 45,
				width: '85%', height: '85%'
			}
		};
		
		let chart = new google.visualization.PieChart(document.getElementById(chartId));
		chart.draw(data, options);
	});
}
