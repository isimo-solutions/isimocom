<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" />
	<xsl:template match="/">
		<html>
		<head><title>Testautomatiserung Kernclient - Abdeckungsbericht</title>
		<script src="jquery-3.3.1.js"></script>
		<script src="treeTable.js"></script>
		
		<script>
		<![CDATA[
		var colors = [
   		 { num: 0.0, color: { r: 0xff, g: 0xff, b: 0xff } },
   		 { num: 20.0, color: { r: 0x99, g: 0xff, b: 0 } },
  		 { num: 200.0, color: { r: 0x00, g: 0xff, b: 0 } } ];

		var getColorForNumber = function(number) {
  		  for (var i = 1; i < colors.length - 1; i++)
      		  if (number < colors[i].num) break;
      	 
      	  var lower = colors[i - 1];
          var upper = colors[i];
    	  var range = upper.num - lower.num;
    	  var rangePct = (number - lower.num) / range;
    	  var pctLower = 1 - rangePct;
    	  var pctUpper = rangePct;
    	  var color = {
        		r: Math.floor(lower.color.r * pctLower + upper.color.r * pctUpper),
        		g: Math.floor(lower.color.g * pctLower + upper.color.g * pctUpper),
        		b: Math.floor(lower.color.b * pctLower + upper.color.b * pctUpper)
    	  };
    	  return 'rgb(' + [color.r, color.g, color.b].join(',') + ')';
   		} 
   		
   		$(document).click(function() {
		        jQuery('tr:visible').filter(':odd').css({'background-color': '#ffffff'});
	   			jQuery('tr:visible').filter(':even').css({'background-color': '#f5f5f5'});
			})

	 window.onload=function(){ 
		var tdActions = document.getElementsByClassName("Total_Actions")
		var tdErrors = document.getElementsByClassName("Errors")
		for(i = 0; i < tdActions.length; i++)
			tdActions[i].style.backgroundColor = getColorForNumber(tdActions[i].innerText);	
		
		for(i = 0; i < tdErrors.length; i++){
			if(tdErrors[i].innerText > 0){
				tdErrors[i].style.backgroundColor = '#FF6666';
			} else{
				tdErrors[i].style.backgroundColor = '#66FF6B';
			}
		}	
			
       jQuery('tr:visible').filter(':odd').css({'background-color': '#ffffff'});
	   jQuery('tr:visible').filter(':even').css({'background-color': '#f5f5f5'});
	}
		
		]]>
		</script>	
		
		<style>
			table {
				font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
				border-collapse: collapse;
				border-style:solid;
				border-width: medium;
			}
			
			td {
			border: 1px solid #ddd;
			padding: 8px;
			}
			
			.aright {
				text-align: right;
			}
			
			tr:hover td.id{
			 background-color: #D2D2D2;
			}
			
			tr:hover td.Total_Actions{
			opacity : 0.7;
			}
			
			.Total_Actions {
			  padding: 2px;
			  background-image: radial-gradient(circle ,rgba(200,200,200,0.1) 5%, rgba(255,255,255,0.5) 90%);
			  list-style: none;
			  text-align: center;
			  color: black;
			}
			
			.Errors {
			  padding: 2px;
			  background-image: radial-gradient(circle ,rgba(200,200,200,0.1) 5%, rgba(255,255,255,0.5) 90%);
			  list-style: none;
			  text-align: center;
			  color: black;
			}
				
			
		</style>
		</head>
		<body>
		<h2>Testabdeckungsbericht</h2>
		<p>Summe einzelnen Actions in <xsl:value-of select="wrapper/@name"/>: <xsl:value-of select="wrapper/stats/@totalActions"/></p>
		<p>Anzahl Fehler gesamt: <xsl:value-of select="wrapper/stats/@totalErrors"/></p>
		<p>Anzahl betroffenen Masken (ohne Tabreiters): <xsl:value-of select="count(wrapper//uimasks/entry)"/></p>
		<p>Anzahl betroffenen Masken (mit Tabreiters): <xsl:value-of select="count(wrapper//*[self::uimasks or self::submasks]/entry)"/></p>
		<script>
		com_github_culmat_jsTreeTable.register(this)
		var options = {
			renderedAttr : {
				id : 'Titel',
				Total_Actions: 'Actions Gesamt',
				Errors: 'Fehler',
				Testcases: 'Testcases'
			},
			
			tableAttributes :{
			 id: 'coverage-table'
			},
			initialExpandLevel : 1
		}
		appendTreetable(makeTree([<xsl:apply-templates select="wrapper/stats/uimasks/entry"><xsl:sort select="key"/></xsl:apply-templates>]), options)
		</script>
		</body>
		</html>

	</xsl:template>
	
	<xsl:template match="entry">
	<xsl:param name="parentID"/>
	<xsl:variable name="key">
				<xsl:choose>
					<xsl:when test="$parentID!=''">
						<xsl:value-of select="concat($parentID,'/',key)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="key"/>
					</xsl:otherwise>
				</xsl:choose>
	</xsl:variable>
				{
					"id" : "<xsl:value-of select="$key"/>",
					"Total_Actions" : "<xsl:value-of select="value/@totalActions"/>",
					"Errors" : "<xsl:value-of select="value/@errors"/>",
					"Testcases" : "<xsl:apply-templates select="value/testcases"/>",
					<xsl:if test="$parentID!=''">
					"parent" : "<xsl:value-of select="$parentID"/>"
					</xsl:if>
				},
				
				<xsl:apply-templates select="value/submasks/entry">
					<xsl:sort select="key"/>
					<xsl:with-param name="parentID" select="$key"/>
				</xsl:apply-templates>
				
		
	</xsl:template>
	
	<xsl:template match="testcases"><xsl:apply-templates/><xsl:if test="following-sibling::testcases">, </xsl:if></xsl:template>
</xsl:stylesheet>