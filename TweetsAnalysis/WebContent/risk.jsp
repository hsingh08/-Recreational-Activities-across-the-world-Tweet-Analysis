<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-3d.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="http://d3js.org/d3.v3.min.js"></script>
<script src="d3.layout.cloud.js"></script>
<script src="show.different.graphs.js"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>	
<link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">
<link href="style.css" rel="stylesheet">
<%
    	   
    	List<String> keys = (List<String>) request.getAttribute("keys");
    	List<Integer> values = (List<Integer>) request.getAttribute("values");
%>
<script>
var series = [];
<%for (int i = 0; i < keys.size(); i++) {%>
    console.log(<%=keys.get(i)%>);
    series.push(['<%=keys.get(i)%>', <%=values.get(i)%>]);
<%}%>
console.log(series);
	
</script>

<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>	
<title>Risky vs Non Risky</title>
</head>
<body>
<div id="banner"></div>
<div id="bodybutton" class="afterClick">
    <br><br> 
    <div> Click on below to see trends</div>
     <div class="row centerRow">     
     <form class="" ><input onclick="showTopActivities()" value="Popular Words" class="btn btn-circlesmall" style="background: rgb(29, 187, 234); opacity:1;"></input></form>
     <form class="" ><input onclick="showTopCountries()" value="By Countries except US" class="btn btn-circlesmall" style="background: rgb(29, 187, 234);"></input></form>
     <form class="" ><input onclick="showAgeGroup()" value="By Age Group" class="btn btn-circlesmall"style="background-color: rgb(228, 80, 19);"></input></form>        
     <form class="" ><input onclick="showHashTags()" value="Top HashTags" class="btn btn-circlesmall"style="background-color: rgb(38, 128, 184);"></input></form>
     <form class="" action="queryRiskyActivities" target="_self"><input type="submit" value="Risky vs non-Risky" class="btn btn-circlesmall"style="background-color: rgb(228, 80, 19); opacity:1;"></input></form>
     </div>
</div>  
<div id="container" class="graphContainer"></div>
</body>
<script language="JavaScript">
    $(document).ready(function() {

    	
       
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'container',
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45
            }
        },
        title:
        {
        	text:'Comparision : People doing Risky vs Non Risky Activities'
        },
        plotOptions: {
            pie: {
            	 innerSize: 100,
                depth: 45,
                showInLegend: false,
                dataLabels: {
                    formatter: function(){
                        console.log(this);
                             this.point.visible = true;
                            return this.key;
                        }
					   }
                    }
                
        },
        
        
        series: [{
        	name:'No of People',
            data: [
            	<%for (int i = 0; i < keys.size() - 1; i++) {%>
        {name:'<%=keys.get(i)%>', y:<%=values.get(i)%>},
    <%}%>
                {name:'<%=keys.get(keys.size() - 1)%>', y:<%=values.get(values.size() - 1)%>}
            ]
        }]
    });
});
</script>
</html>