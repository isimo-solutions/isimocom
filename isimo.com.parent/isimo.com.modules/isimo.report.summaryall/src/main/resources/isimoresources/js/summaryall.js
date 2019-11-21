window.columns1 =  {green : 6, red: 7, grey: 8};
window.columns2 =  {Blocker : 1, Kritisch: 2, Wichtig: 3, Standard: 4, Gering: 5, Trivial: 6};
window.criteria = ["Testsuite","Browser","Umgebung","Modul","Mandant ID"];
window.defaultshowcolumns = ["Browser","Umgebung","Modul","Erfolge","Fehlschläge","Testfehler","Gesamt"];
window.defaultforcolumns = {"Browser": "internetExplorer", "Umgebung": "Testauto_trunk_ohne_ZAS"};
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
		Blocker: '#000000',
		Kritisch: '#581845',
		Wichtig: '#900C3F',
		Standard: '#C70039',
		Gering: '#FF5733',
		Trivial: '#FFC300'
};


		window.onload = function() {
			$('input#changeconfig').prop('checked', false);
			createCriteria();
			resetCriteria();
			$("#reset").click(resetCriteria);
			$("select.criteria").change(updateChart);
			$("input#changeconfig").change(updateChart);
			updateChart();
			calcIssuesConfig(window.columns2);
		};
		
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
							text: 'Verteilung der Testfallergebnissen'
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
				var value = o.text();
				if((window.defaultforcolumns[dropdown.attr('criteriaName')] == value) && !detailansicht()) {
					o.attr('selected','true');
				} else {
					o.removeAttr('selected');
				}
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
					if(criteriaName == 'Env (technisch)')
						envname = columnValue;
					visible = visible && (reqValue===columnValue);
				});
				var envname = tr.find("td:nth-child("+window.criteriaMetadata['Umgebung'].pos+")").text();
				if(visible) {
					tr.addClass("datarow");
					tr.removeClass("hidden");
				} else {
					tr.removeClass("datarow");
					tr.addClass("hidden");
				}
			});
		}
		
		function detailansicht() {
			return $('input#changeconfig').is(':checked');
		}
		
		function configureColumn(i) {
			var showbydefault = window.columnMetadata[i]['showbydefault'];
			var show = showbydefault || detailansicht();
			var allcellsselector = 'table#results td:nth-child('+i+'), table#results th:nth-child('+i+')';
			if(show) {
				$(allcellsselector).show();
			} else {
				$(allcellsselector).hide();
			}
		}
		
		function configureColumns() {
			$('table#results tr th').each(function(i) {configureColumn(i+1)});
		}
		
		function updateChart() {
			readChartCriteria();
			updateRowsBasedOnChartCriteria();
			calcSums();
			calcConfig(window.columns1);
			configureColumns();
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
		$('table#results tr th').each(function(i) {
			var name = $(this).text();
			window.columnMetadata[i+1] = { name: name,
				showbydefault: ($.inArray(name, window.defaultshowcolumns) >= 0)
			}
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
		var alle = $("<option id=\"__alle__\">--Alle--</option>");
		$(select).append(alle);
		$.each(valuesset, function(value) {
			var def = "";
			if((window.defaultforcolumns[criteriaName] == value) && !detailansicht())
				def = "selected=\"true\"";
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
			var title = $("tr th:nth-child("+index+")").html();
			bColors.push(window.chartColors[color]);
			var sumforcolor = 0;
			$("tr.datarow td:nth-child("+index+")").each(function(cnt) {
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
						text: 'Verteilung der Fehlerprioritäten'
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
		
		
		$.each( columns, function(color) {
			var index = columns[color];
			var title = $("#issues tr th:nth-child("+index+")").html();
			bColors.push(window.issuesColors[color]);
			var sumforcolor = Number($("#issues tr.datarow td:nth-child("+index+")").text());
			datas.push(sumforcolor);
			labels.push(title+" "+sumforcolor);
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
		if(button.innerText =="Mehr"){
			button.innerText = "Weniger";
			thIssuesDetailsSummary.style.width = "30%";
			thIssuesDetailsTests.style.width = "70%";
			style = "";		
		}
		else{
			button.innerText = "Mehr";
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
	