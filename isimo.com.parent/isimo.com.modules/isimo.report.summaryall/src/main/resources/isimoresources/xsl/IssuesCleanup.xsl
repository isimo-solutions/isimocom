<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="jira.url"/>
<xsl:output method="xml"/>
<xsl:template match="/">
<html>
<head><title>Issues status report</title>
<script>
<![CDATA[

function OptionExist(select, option){
	for(k = 0; k < select.options.length ; k++){
		if(select.options[k].value == option) return true;
	}
	return false;
}

window.onload=function() {
	var table, tr;
	table = document.getElementById('td2');
	var selectJiraNr = document.getElementById('selectJiraNr');
	var selectStatus = document.getElementById('selectStatus');
	if(selectJiraNr.options.length == 0) selectJiraNr.appendChild(new Option('all','all'));
	if(selectStatus.options.length == 0)selectStatus.appendChild(new Option('all','all'));
	for (i = 1; i < table.rows.length; i++) {
		var tr = table.rows[i];
		var tdJiraNr = tr.cells[1];
		var tdStatus = tr.cells[3];
		if(tdJiraNr){
			if(!OptionExist(selectJiraNr, tdJiraNr.innerText)){ 
			selectJiraNr.appendChild(new Option(tdJiraNr.innerText,tdJiraNr.innerText));
			}
		}
		if(tdStatus){
			if(!OptionExist(selectStatus, tdStatus.innerText)){
				selectStatus.appendChild(new Option(tdStatus.innerText,tdStatus.innerText));
			}
		}
		if(tdStatus.innerHTML=="Resolved") tdStatus.style.backgroundColor = '#00ff0022';
		if(tdStatus.innerHTML=="Need verification") tdStatus.style.backgroundColor = '#ffff0055';
	}

}

function Filter(){
	var table = document.getElementById('td2');
	var filterJiraNr = document.getElementById('selectJiraNr').value;
	var filterStatus = document.getElementById('selectStatus').value;
	var color = true; 
	for (i = 1; i < table.rows.length; i++) {
		var tr = table.rows[i];
		var tdJiraNr = tr.cells[1];
		var tdStatus = tr.cells[3];
	
		if((filterJiraNr == "all" || tdJiraNr.innerText == filterJiraNr) && (filterStatus == "all" ||tdStatus.innerText == filterStatus)){
			tr.style.display ="";
			if(color){
				tr.style.backgroundColor = '#f2f2f2';
				color = false;
			}else {
				tr.style.backgroundColor = '#ffffff';
				color = true;
			}
		} else tr.style.display = "none";
	}
}
]]>
</script>

<style>
table {
	border-style:solid;
    border-width: medium;
    font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
    border-collapse: collapse;
	width:100%;
}

tr:nth-child(even){background-color: #f2f2f2;}

tr:hover {background-color: #ddd!important;}

th {
    border: 1px solid black;
    font-weight: bold;
}

td {
	border:1px solid #ddd;
	padding: 8px;
}

</style>
</head>
<body>
<h2><span id="headerSpan">Executed actions with JIRA issue without failure</span>:</h2>
<table style="border-width: 1px; table-layout:fixed;">
<tr>
	<td><span id="resolvedSpan">Resolved</span>: </td>
	<td><xsl:value-of select="count(//occs[@status='Resolved'])"/></td>
</tr>
<tr>
	<td><span id="verificationSpan">Need verification</span>: </td>
	<td><xsl:value-of select="count(//occs[@status='Need verification'])"/></td>
</tr>											
<tr>
	<td><span id="unresolvedSpan">Unresolved</span>: </td>
	<td><xsl:value-of select="count(//occs[@status='Unresolved'])"/></td>
</tr>
<tr>
	<td><span id="infoTableSumSpan">Sum</span>: </td>
	<td><xsl:value-of select="count(//occs)"/></td>
</tr>
<tr>
<td><span id="infoTableUniqueSpan">Unique issues</span>: </td>
<td><xsl:value-of select="//issuesnumber"/></td>
</tr>
</table>
<table id="td2">
<tr>
<th style="min-width: 70%;"><span id="issuesTableScenarioSpan">Scenario</span></th>
<th><div><span id="issuesTableIdSpan">Jira-Id</span></div><select id="selectJiraNr" onchange="Filter()"/></th>
<th><span id="issuesTableLineNummerSpan">Line Number</span></th>
<th><div><span id="issuesTableStatusSpan">Status</span></div><select id="selectStatus" onchange="Filter()"/></th>
</tr>
<xsl:apply-templates select="//occs"/>
</table>
</body>
</html>

	
</xsl:template>

<xsl:template match="//occs">


<tr>
<td><xsl:value-of select="@scenario"/></td>
<td><a href="{jira.url}/{@issue}"><xsl:value-of select="@issue"/></a></td>
<td><xsl:value-of select="@linenumber"/></td>
<td><span id="{@statusTranslateId}"><xsl:value-of select="@status"/></span></td>
</tr>
</xsl:template>
</xsl:stylesheet>