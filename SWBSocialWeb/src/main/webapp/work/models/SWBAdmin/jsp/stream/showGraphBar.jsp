<%-- 
    Document   : graphBarFilter
    Created on : 16/09/2013, 07:31:57 PM
    Author     : gabriela.rosales
--%>

<%@page import="java.net.URLEncoder"%>
<%@page import="com.sun.mail.handlers.image_gif"%>
<%@page import="org.semanticwb.platform.SemanticObject"%>
<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.social.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.semanticwb.SWBUtils"%>
<%@page import="org.semanticwb.model.*"%>
<%@page import="org.semanticwb.model.*"%>
<%@page import="org.semanticwb.SWBPlatform"%>
<%@page import="org.semanticwb.SWBPortal"%> 
<%@page import="org.semanticwb.platform.SemanticProperty"%>
<%@page import="org.semanticwb.portal.api.*"%>
<%@page import="java.util.*"%>
<%@page import="org.semanticwb.social.util.*"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest"/>
<!DOCTYPE html>
<%
    String suri = request.getParameter("suri");
    if (suri == null) {
        return;
    }
    SemanticObject semObj = SemanticObject.getSemanticObject(suri);
    if (semObj == null) {
        return;
    }

    String args = "?objUri=" + semObj.getEncodedURI();

    String selectedAnio = request.getParameter("selectedAnio");
    String selectAnio = request.getParameter("selectAnio");
    String selectMes = request.getParameter("selectMes");
    
    args += "&selectedAnio=" + selectedAnio;
    args += "&selectAnio=" + selectAnio;
    args += "&selectMes=" + selectMes;
    String args2 = "&selectedAnio=" + selectedAnio;
    args2 += "&selectAnio=" + selectAnio;
    args2 += "&selectMes=" + selectMes;
    SWBResourceURL urlRender = paramRequest.getRenderUrl();
%>


<html>
    <head>
        <script src="http://d3js.org/d3.v3.min.js"></script>
        <script src="http://labratrevenge.com/d3-tip/javascripts/d3.tip.min.js"></script>
        <meta charset="utf-8">
        <style type="text/css">
            body {
                font: 10px sans-serif;
            }

            .axis path, .axis line {
                fill: none;
                stroke: #FF8000;
                shape-rendering: crispEdges;
            }
            .bar {
                fill: orange;
            }

            .bar:hover {
                fill: orange ;
            }

            .bar_neutrals {
                fill: #838383;
            }

            .bar_neutrals:hover {
                fill: #838383 ;
            }

            .bar_negatives {
                fill: #FF0000;
            }

            .bar_negatives:hover {
                fill: #FF0000;
            }


            .bar_positives {
                fill: #008000;
            }

            .bar_positives:hover{
                fill: #008000 ;
            }            
            .x.axis path {
                display: none;
            }

            .d3-tip {
                line-height: 1;
                font-weight: bold;
                padding: 12px;
                background: rgba(0, 0, 0, 0.8);
                color: #fff;
                border-radius: 2px;
            }

            /* Creates a small triangle extender for the tooltip */
            .d3-tip:after {
                box-sizing: border-box;
                display: inline;
                font-size: 10px;
                width: 100%;
                line-height: 1;
                color: rgba(0, 0, 0, 0.8);
                content: "\25BC";
                position: absolute;
                text-align: center;
            }

            /* Style northward tooltips differently */
            .d3-tip.n:after {
                margin: -1px 0 0 0;
                top: 100%;
                left: 0;
            }

            .excel{
                background-image:url(/swbadmin/css/images/ico-exp-excel.png); 
                background-repeat:no-repeat; 
                /*background-position:4px 3px;*/
                background-position: center; 
                /*height: 30px;
                width: 33px;*/
                text-indent:-9999px
            }
            .aShowGraph a {
              display: inline-block;
              height: 30px;
              width: 33px;
              /*border: solid 1px #373f42;*/
              /*background-color: #434c50;*/
              /*float: left;*/
              border-radius: 4px;
              -moz-border-radius: 4px;
              -webkit-border-radius: 4px;
              -khtml-border-radius: 4px;
              
            }
            .aShowGraph {
                padding-bottom: 0px;
            }
        </style>
        <link href="/swbadmin/css/swbsocial.css" rel="stylesheet" type="text/css">
    </head>
    <body onload="firstLoad();resizeIframe();">        <!--javascript:valid('1');-->
        <div align="center" class="aShowGraph">
            <div id="titleChange" style="margin-left: 100px; width: 700px">
                <h1>MENCIONES POR MES</h1>

            </div>
            <div align="center">
                <a href="javascript:exportFile();" 
			onclick="return confirm('&iquest;Desea exportar a excel?')" class="excel">Exportar excel</a>
            </div>
        </div>
        <div id="chart">
            
        </div>
        <script type="text/javascript" >
            function firstLoad() {
            var margin = {top: 20, right: 20, bottom: 30, left: 40},
            width = 960 - margin.left - margin.right,
            height = 500 - margin.top - margin.bottom;

            var x = d3.scale.ordinal().rangeRoundBands([1, width]);
            var y = d3.scale.linear().range([height, 0]);
            
            var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");
            //.tickSize(0);

            var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");
                          
            var tip = d3.tip()
            .attr('class', 'd3-tip')
            .offset([-10, 0])
            .html(function(d) {
                return "<strong>Numero de post:</strong> <span style='color:#FFFFFF'>" + d.post + "</span><br>\n\
                            <strong>Neutros:</strong>  <span style='color:#D8D8D8'>" + d.neutrals + "</span><br>  \n\
                            <strong>Positivos:</strong>  <span style='color:#04B431'>" + d.positives + "</span><br> \n\
                            <strong>Negativos:</strong>  <span style='color:red'>" + d.negatives + "</span><br>           ";
            })
            //});        //var data = [[1,100],[6,20],[20, 50]];

            var xArray = new Array();

            d3.json("<%=SWBPlatform.getContextPath()%>/work/models/<%=SWBContext.getAdminWebSite().getId()%>/jsp/stream/InfoGraphBar.jsp<%=args%>", function(error, data) {
         
                //function(d) { alert('entro');return x(data.day);}
                // alert(data[1].x);
                var typeX ;
                var neutrals ;
                var positives;
                var negatives; 
                
                for (i = 0; i < data[1].x; i++) {               
                    xArray.push(data[i].month)                    
                    y.domain([0,data[i].totalPost]);               
                    typeX =  data[i].typeX;
                   
                }  
                x.domain(xArray);                                      

                var svg = d3.select("#chart").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
         
                svg.call(tip);
                svg.append("g")
                .attr("class", "axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("Numero de Post");           

        
                svg.append("g")
                .attr("class", "axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis)
                .append("text")
                .attr("x", 910)
                .attr("dx", ".20em")
                .style('text-anchor','end')
                .text(typeX);
             
             

               
                svg.selectAll("bar")
                .data(data)
                .enter().append("rect")
                .attr("class",function(d) { 
                    var neutrals = d.neutrals;
                    var positives = d.positives;
                    var negatives = d.negatives;
                    if(positives>negatives && positives>neutrals){
                        return "bar_positives"; 
                    }else if(negatives>neutrals){                        
                        return "bar_negatives";                        
                    }else {
                        return "bar_neutrals";                        
                    }        
                    
                })
                .attr("x", function(d) { return x(d.month);})
                .attr("width", x.rangeBand() - 5)
                .attr("y", function(d) {return y(d.post);})
                .attr("height", function(d) {return height - y(d.post);}) 
                .on('mouseover', tip.show)
                .on('mouseout', tip.hide)
                .on("click", function(d) {
                    <%
                    if (request.getParameter("selectMes") != null && !request.getParameter("selectMes").trim().isEmpty()){                        
                    %>                        
                        //var urlParams = '&selectedAnio='+d.year+'&selectedMes='+<%--=request.getParameter("selectMes")--%>+'&selectedDia='+d.day;
                        //parent.postHtml('<%--=urlRender.setMode("showBarByDay")%>?suri=<%=URLEncoder.encode(suri)--%>' + urlParams, 'postInByHour');
                        var url = "<%=urlRender.setMode("exportExcel").setParameter("type", "graphBar").setCallMethod(SWBParamRequest.Call_DIRECT).setParameter("suri", suri)%>&selectedAnio="+d.year+"&selectMes="+<%=request.getParameter("selectMes") %>+"&selectAnio="+d.year+"&selectDay="+d.day+"&selectMonth2="+d.month2;
                        document.location.href = url;
                    <%}else{%>
                        var url = "<%=urlRender.setMode("exportExcel").setParameter("type", "graphBar").setCallMethod(SWBParamRequest.Call_DIRECT).setParameter("suri", suri)%>&selectedAnio="+d.year+"&selectMes="+d.month+"&selectAnio="+d.year+"&selectDay="+d.day+"&selectMonth2="+d.month2;
                        document.location.href = url;
                    <%}%>
                }) 
            });
            }
        </script>
        <script language="javascript" type="text/javascript">
            function resizeIframe() {
                var iframe =  window.parent.document.getElementById('inneriframe');              
                var container = document.getElementById('chart');  
                var sizeIframe = 600;
                if(container.offsetHeight !== 0) {
                    sizeIframe = container.offsetHeight;
                }
                iframe.style.height = sizeIframe + 'px';    
                if("<%=request.getParameter("selectMes")%>" !== "" && "<%=request.getParameter("selectAnio")%>" !== "") {
                    document.getElementById("titleChange").innerHTML="MENCIONES POR D&iacute;A";
                } else {
                    document.getElementById("titleChange").innerHTML="MENCIONES POR MES";
                }
            }
            
            function exportFile() {
                if(JSON.stringify(window.parent.document.getElementById('divAnual').style.display) === '"block"') {
                    if(window.parent.document.getElementById("selectAnio").value === '') {
                        alert("Seleccione el a\u00f1o");//&ntilde;
                        return;
                    }
                } else if(JSON.stringify(window.parent.document.getElementById('divAnualMensual').style.display) === '"block"') {
                    if(window.parent.document.getElementById("selectAnio2").value === '') {
                        alert("Seleccione el a\u00f1o");//&ntilde;
                        return;
                    }
                    if(window.parent.document.getElementById("selectMes").value === '') {
                        alert("Seleccione el mes");//&ntilde;
                        return;
                    }
                }
                var url = '<%=urlRender.setMode("exportExcel").setParameter("type", "graphBar2").setCallMethod(SWBParamRequest.Call_DIRECT).setParameter("suri", suri)%>';
                url += '<%=args2%>';
                document.location.href = url;
            }
        </script>
</html>