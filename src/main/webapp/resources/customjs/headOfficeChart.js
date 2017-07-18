Highcharts.chart('graph', {
    chart: {
        type: 'areaspline'
    },
    title: {
        text: ' '
    },
    legend: {
        layout: 'vertical',
        align: 'left',
        verticalAlign: 'top',
        x: 150,
        y: 100,
        floating: true,
        borderWidth: 1,
        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
    },
    xAxis: {
        categories: [
            'M',
            'T',
            'W',
            'T',
            'F',
            'S',
            'S'
        ],
        plotBands: [{ // visualize the weekend
            from: 4.5,
            to: 6.5,
            color: 'rgba(68, 170, 213, .2)'
        }]
    },
    yAxis: {
        title: {
            text: ' '
        }
    },
    tooltip: {
        shared: true,
        valueSuffix: ' units'
    },
    credits: {
        enabled: false
    },
    plotOptions: {
        areaspline: {
            fillOpacity: 0.5
        }
    },
    series: [{
        name: 'John',
        data: [3, 4, 3, 5, 4, 10, 12]
    }, {
        name: 'Jane',
        data: [1, 3, 4, 3, 3, 5, 4]
    }]
});

Highcharts.chart('femaleGraph', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: 0,
        plotShadow: false
    },
    title: {
        text: '<br>female<br>Visitor',
        align: 'center',
        verticalAlign: 'middle',
        y: 40
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            dataLabels: {
                enabled: true,
                distance: -50,
                style: {
                    fontWeight: 'bold',
                    color: 'white'
                }
            },
            startAngle: -90,
            endAngle: 90,
            center: ['50%', '75%']
        }
    },
    credits: {
        enabled: false
    },
    series: [{
        type: 'pie',
        name: 'Browser share',
        innerSize: '50%',
        data: [
            [' ',   10.38],
            [' ',       56.33],
            [' ', 24.03],
            [' ',    4.77],
            [' ',     0.91],
            {
                name: 'Proprietary or Undetectable',
                y: 0.2,
                dataLabels: {
                    enabled: false
                }
            }
        ]
    }]
});

Highcharts.chart('maleGraph', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: 0,
        plotShadow: false
    },
    title: {
        text: '<br>male<br>Visitor',
        align: 'center',
        verticalAlign: 'middle',
        y: 40
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            dataLabels: {
                enabled: true,
                distance: -50,
                style: {
                    fontWeight: 'bold',
                    color: 'white'
                }
            },
            startAngle: -90,
            endAngle: 90,
            center: ['50%', '75%']
        }
    },
    credits: {
        enabled: false
    },
    series: [{
        type: 'pie',
        name: 'Browser share',
        innerSize: '50%',
        data: [
            [' ',   10.38],
            [' ',       56.33],
            [' ', 24.03],
            [' ',    4.77],
            [' ',     0.91],
            {
                name: 'Proprietary or Undetectable',
                y: 0.2,
                dataLabels: {
                    enabled: false
                }
            }
        ]
    }]
});

$(document).ready(function(){
	
	var trVal = $(".listBody").find("tr:first").find("td:first").text();
//	alert(trVal);
	
	$.ajax({
		type:"get",
		url:"daySales",
		data:{
			bhf_code:1
		},
//		dataType:"json",
		success:function(data){
			
			$("#branchOffice").empty();
			
			data = JSON.parse(data);
				
			var length = data.result.length;
			
			if(length <= 0){
				$("#branchOffice").text("매출이 존재하지 않습니다.");
				
					
			}else{
				$("#branchOffice").css("margin-top","1%")
				var options = {
					title : {
						text : '일매출'
					},
					chart: {
			            inverted: true,
			            polar: false
			        },
					subtitle : {
						text : ''
					},
					xAxis : {
						categories : []
					},
					yAxis : {
						title: {
				            text: ' '
				        }
					},
					credits: {
					    enabled: false
					},
					series : [ {
						type : 'column',
						colorByPoint : true,
						data : [],
						showInLegend : false
					} ]

				}

				for (var i = 0; i < length; i++) {

					options.xAxis.categories[i] = data.result[i].bill_issu_de;
					options.series[0].data[i] = parseInt(data.result[i].totalPrice);

				}

				chart = Highcharts.chart('branchOffice', options);
				
				}

			}
	});
});

$(".searchingBtn").on("click", function(){
	
	var bhf_nm = $(".bhf_nm").val()
	
//	alert(bhf_nm);
	
	$.ajax({
		type:"get",
		url:"bhfSearching",
		data:{
			bhf_nm:bhf_nm
		},
		dataType:"json",
		
		success:function(data){			
			if(data!=null){
				$(".listBody").empty();
				
				var length = data.result.length;
				
				for(var i=0; i<length; i++) {
					
					var branch_list = $("<tr class='branch'></tr>");
//					$("<td><input type='checkbox' class='checked'></td>").appendTo(branch_list);
					$("<td></td>").addClass("bhf_code").text(data.result[i].bhf_code).appendTo(branch_list);
					$("<td></td>").addClass("bhf_nm").text(data.result[i].bhf_nm).appendTo(branch_list);
					$("<td></td>").addClass("bhf_telno").text(data.result[i].bhf_telno).appendTo(branch_list);
					
					branch_list.appendTo($(".listBody"));
					
					clickTr();
				}
				
			}else {
				$("<td></td>").text("검색된 물품이 없습니다.").appendTo(branch_list);
//				alert('hii');
			}
		},
		error:function(){
		}
	});
});

function clickTr() {
	
		$('.listBody tr').on("click",function(){
	
			var trVal = $(this).find("td:first").text();
//			alert(trVal);
			
			$.ajax({
				type:"get",
				url:"daySales",
				data:{
					bhf_code:trVal
				},
//				dataType:"json",
				success:function(data){
					
					$("#branchOffice").empty();
					
					data = JSON.parse(data);
						
					var length = data.result.length;
					
					if(length <= 0){
						$("#branchOffice").text("일주일 간의 매출이 존재하지 않습니다.").css("line-height", "400px");
						
							
					}else{
						$("#branchOffice").css("margin-top","1%")
						var options = {
							title : {
								text : '일매출'
							},
							chart: {
					            inverted: true,
					            polar: false
					        },
							subtitle : {
								text : ''
							},
							xAxis : {
								categories : []
							},
							yAxis : {
								title: {
						            text: ' '
						        }
							},
							credits: {
							    enabled: false
							},
							series : [ {
								type : 'column',
								colorByPoint : true,
								data : [],
								showInLegend : false
							} ]

						}

						for (var i = 0; i < length; i++) {

							options.xAxis.categories[i] = data.result[i].bill_issu_de;
							options.series[0].data[i] = parseInt(data.result[i].totalPrice);

						}

						chart = Highcharts.chart('branchOffice', options);
						
					}

				}
			});
		});
}

clickTr();
