window.columns1 =  {green : 4, red: 5, grey: 6};
window.criteriaIndex = [1,2,3]
window.criteria = [];
window.columnMetadata = {};
window.criteriaMetadata = {};
Chart.defaults.global.legend.position = 'right';
Chart.defaults.global.legend.labels.boxWidth = 15;
Chart.defaults.global.legend.labels.padding = 25;


window.chartColors = {
	red: 'rgb(255, 00, 00)',
	orange: 'rgb(255, 159, 64)',
	yellow: 'rgb(255, 205, 86)',
	green: 'rgb(50, 180, 50)',
	blue: 'rgb(54, 162, 235)',
	purple: 'rgb(153, 102, 255)',
	grey: 'rgb(100, 100, 100)'
};
window.issuesColors = {
		5: '#000000',
		4: '#581845',
		3: '#900C3F',
		2: '#C70039',
		1: '#FF5733',
		0: '#FFC300'
};


		window.onload = function() {
			calcCriteria();
			calcMetadata();
			createCriteria();
			resetCriteria();
			$("select.criteria").change(updateChart);
			updateChart();
			if($("#chart-area-issues").length) calcIssuesConfig(window.columns2);
		};
		
		function calcCriteria(){
				$.each(window.criteriaIndex, function(index){
					window.criteria.push($("table#results tr th:nth-child("+window.criteriaIndex[index]+")").text())
				});
		}
		function initData() {
			window.config = {
					type: 'pie',
					data: {
						datasets: [{
							data: [
								1,2,3
							],
							backgroundColor: [
								window.chartColors.red,
								window.chartColors.orange,
								window.chartColors.green,
							],
							label: 'Dataset 1'
						}],
						labels: [
							'Red',
							'Orange',
							'Yellow',
						]
					},
					options: {
						responsive: true,
						title: {
							display: true,
							text: $("#testsChartTitleSpan").text()
						 },
						 layout: {
					            padding: {
					                left: 100,
					                right: 100,
					                top: 0,
					                bottom: 0
					            }
						 },
						 plugins: {
							 datalabels: {
								 color: 'white',
								 display: function(context) {
									 return context.dataset.data[context.dataIndex] > 0;
								 },
					             labels: {
				                    title: {
				                        font: {
				                            weight: 'bold'
				                        }
				                    }
					             } 
							 }
						 }
					}
				};
		}
		
		function resetDropdown() {
			var dropdown = $(this);
			dropdown.change(function() {});
			var resetTo = "--Alle--";
			dropdown.find("option").each(function(option) {
				var o = $(this);
				o.removeAttr('selected');
			});
		}
		
		function resetCriteria() {
			initData();
/*
			$("select.criteria").each(resetDropdown);
			$("select.criteria").val("--Alle--");
			$("select.criteria").change(updateChart);*/
			$("select.criteria").each(resetDropdown);
			updateChart();
		}
		
		function readChartCriteria() {
			initData();
			window.chartCriteria = {};
			$("select.criteria").each(function() {
				var selected = $(this).find("option:selected").text();
				if(selected !== "--Alle--")
					window.chartCriteria[$(this).attr('criteriaName')] = selected.toString();
			});
		}
		
		function calcSum() {
			var columnNumber = $(this).prevAll().length+1;
			var sum = 0;
			$("tr.datarow td:nth-child("+columnNumber+")").each(function() {
				sum += Number($(this).text());
			});
			$(this).text(sum);
		}
		
		function calcSums() {
			$("td[sum=yes]").each(calcSum);
		}
		
		function updateRowsBasedOnChartCriteria() {
			$("table#results tr:not(.sumrow)").each(function() {
				var tr = $(this);
				var visible = true;
				if(tr.find("th").length > 0)
					return;
				$.each(window.chartCriteria,function(criteriaName) {
					var reqValue = this.toString();
					var columnValue = tr.find("td:nth-child("+window.criteriaMetadata[criteriaName].pos+")").text();
					visible = visible && (reqValue === "--All--" ||reqValue===columnValue);
				});
				if(visible) {
					tr.addClass("datarow");
					tr.removeClass("hidden");
				} else {
					tr.removeClass("datarow");
					tr.addClass("hidden");
				}
			});
		}
		
		function updateChart() {
			readChartCriteria();
			updateRowsBasedOnChartCriteria();
			calcSums();
			calcConfig(window.columns1);
			var ctx = document.getElementById('chart-area-tests').getContext('2d');
			window.myPie = new Chart(ctx, window.config);
		}
		
	
	function calcMetadata() {
		$.each( window.criteria, function() {
			var i = 0;
			var name = this.toString();
			$("table#results tr th").each(function(th) {
				i++;
				if($(this).text() === name) {
					window.criteriaMetadata[name] = { pos : i };
				}					
			});
		});
	}
	
	function createSingleDropdown(criteriaidx) {
		var criteriaName = window.criteria[criteriaidx];
		var div = $("<div class=\"select\" criteriaName=\""+criteriaName+"\"></div>");
		
/*		var divlabel = document.createElement("div");
		$(div).append(divlabel);
		divlabel.setAttribute("class","label");
		divlabel.text = criteriaName;
		var divselect = document.createElement("div");
		div.append(divselect);
		divselect.setAttribute("class","select");
		var select = document.createElement("select");
		divselect.append(select);
		select.setAttribute("class","criteria");
		select.setAttribute("criteriaName",criteriaName);

		*/
		
		var select = $("<select class=\"criteria\" criteriaName=\""+criteriaName+"\"></select>");
		div.append(select);
		var rownumber = window.criteriaMetadata[criteriaName]["pos"];
		var valuesset = {};
		$("table#results tr:not(.sumrow) td:nth-child("+rownumber+")").each(function(){
			valuesset[$(this).text()] = $(this).text();
		});
		var alle = $("<option id=\"__all__\">--All--</option>");
		$(select).append(alle);
		$.each(valuesset, function(value) {
			var def = "";
			var option = $("<option id=\""+value+"\" "+def+">"+this+"</option>");
			/*var option = document.createElement("option");
			option.setAttribute("id",value);
			option.text = this;*/
			$(select).append(option);
		});
		$("table#results tr th:contains('"+criteriaName+"')").append(div);
	}
	
	function createCriteria() {
		calcMetadata();
		$.each(window.criteria, createSingleDropdown);
	}
		
	function calcConfig(columns) {
		var bColors = [];
		var datas = [];
		var labels = [];
		var titles = [];

		$.each( columns, function(color) {
			var index = columns[color];
			var title = $("#results tr th:nth-child("+index+")").text();
			bColors.push(window.chartColors[color]);
			var sumforcolor = 0;
			$("#results tr.datarow td:nth-child("+index+")").each(function(cnt) {
				sumforcolor += Number($( this ).text());
			});
			datas.push(sumforcolor);
			labels.push(title+" "+sumforcolor);
			titles.push(title);
		});
		window.config.data = {
			datasets: [{
				data: datas,
				backgroundColor: bColors,
				label: 'Dataset 1'
			}],
			labels: labels,
		};
	};
	
	function calcIssuesConfig(columns){
		window.issuesConfig = {
				type: 'pie',
				data: {
					datasets: [{
						data: [
							1,2,3
						],
						backgroundColor: [
							window.chartColors.red,
							window.chartColors.orange,
							window.chartColors.green,
						],
						label: 'Dataset 1'
					}],
					labels: [
						'Red',
						'Orange',
						'Yellow',
					]
				},
				options: {
					responsive: true,
					title:{
						display: true,
						text:  $("#issuesChartTitleSpan").text()
					 },
					 layout: {
				            padding: {
				                left: 100,
				                right: 100,
				                top: 0,
				                bottom: 0
				            }
					 },
					 plugins: {
						 datalabels: {
							 color: 'white',
							 display: function(context) {
								 return context.dataset.data[context.dataIndex] > 0;
							 },
				             labels: {
			                    title: {
			                        font: {
			                            weight: 'bold'
			                        }
			                    }
				             } 
						 }
					 }
				}
			};
		
		var bColors = [];
		var datas = [];
		var labels = [];
		
		var i = 0
		$.each( $("#issues tr th"), function(color) {
			var index = i + 1;
			var title = $("#issues tr th:nth-child("+index+")").text();
			bColors.push($("#issues tr.colorrow td:nth-child("+index+")").text());
			var sumforcolor = Number($("#issues tr.datarow td:nth-child("+index+")").text());
			datas.push(sumforcolor);
			labels.push(title+" "+sumforcolor);
			i = i + 1;
		});
		window.issuesConfig.data = {
			datasets: [{
				data: datas,
				backgroundColor: bColors,
				label: 'Dataset 1'
			}],
			labels: labels
		};
		
		
		var ctx = document.getElementById('chart-area-issues').getContext('2d');
		window.myPieIssues = new Chart(ctx, window.issuesConfig);
		
		
	}
	
	function ShowDetaliedTests(){
		var tdDetailedTests = document.getElementsByClassName("teststd");
		var thIssuesDetailsSummary = document.getElementsByClassName("issuesDetailsSummary")[0];
		var thIssuesDetailsTests = document.getElementsByClassName("issuesDetailsTests")[0];
		var moduletd = document.getElementsByClassName("moduletd");
		
		var button = document.getElementById("detailedTestsButton");
		var style = "";
		if(button.innerText == $("#detailedIssuesListMoreButtonSpan").text()){
			button.innerText = $("#detailedIssuesListLessButtonSpan").text()
			thIssuesDetailsSummary.style.width = "30%";
			thIssuesDetailsTests.style.width = "70%";
			style = "";		
		}
		else{
			button.innerText = $("#detailedIssuesListMoreButtonSpan").text();
			thIssuesDetailsSummary.style.width = "70%";
			thIssuesDetailsTests.style.width = "30%";
			style = "none";
		}
		
		for(i = 0; i < tdDetailedTests.length; i++){
			tdDetailedTests[i].style.display = style;
			if(style == "none") moduletd[i].style.width = "100%";
			else moduletd[i].style.width = "30%";
		}
	
	}