<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="name"/>
<xsl:param name="project"/>
<xsl:param name="TSTAMP"/>
<xsl:param name="faliurePercent"/>
<xsl:param name="jira.url"/>
<xsl:param name="generate.coverage"/>
<xsl:param name="generate.issuescleanup"/>
<xsl:output method="xml"/>
<xsl:template match="summaryall">
<html>
<head><title><xsl:value-of select="$name"/></title>
<style>
table {
    font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
    border-collapse: collapse;
    width: 100%;
}

table td, table th {
    border: 1px solid #ccc;
    padding: 8px;
}

table tr:nth-child(even){background-color: #f2f2f2;}

table tr:hover {background-color: #ddd;}

table th {
    padding-top: 12px;
    padding-bottom: 12px;
    text-align: left;
    background-color: #aaaaaa;
    color: white;

}
.hidden {
	display: none;
} 
.row {
  width: 100%;
  margin: 0 auto;
}
.block {
  float: left;
}

.header{
	font-size: 25px;
	font-weight:bold;
	width:100%; 
	display:flex; 
	align-items: center;
	padding-bottom: 20px;
}

.status_succes{
	color: #00c300;
	padding-left: 30px;
	padding-right: 30px;
	border-radius: 25px;
	text-align: center;
	font-size: 40px;
	background-color: #b3ffb3;
}
.test_fail{
	color: red;
	padding-left: 15px;
	padding-right: 15px;
	background-color: #ffe4c4;
	border-radius: 25px;
	flex-grow:0!important;
}

.status_fail{
	padding-left: 15px;
	padding-right: 15px;
	text-align: center;
	font-size: 40px;
}

.inner_felxbox{
	display:flex; 
	flex-grow:1;
	align-items: center;
}

.fail_reasons{
	flex-direction: column;
	align-items: flex-start;
	padding-left: 10px;
	font-size: 17px;
	color: red;
	font-weight: normal;
}

.issuesDetails{
	table-layout:fixed;
	width: 100%;
}
.issuesDetails td, .issuesDetails th{
	text-align:center;
	padding: 10px;
}
.summary{
	text-align: left!important;
}

.issuesDetailsId{
	width: 80px;
}
.issuesDetailsSummary{
	width: 70%;
}
.issuesDetailsPriority{
	width: 80px;
}
.issuesDetailsStatus{
	width: 90px;
}
.issuesDetailsSprint{
	width: 150px;
}
.issuesDetailsOccurenceCount{
	width: 50px;
}
.issuesDetailsTests{
	width: 30%;
}

.innerIssuesDetails{
	width:100%;
	table-layout:fixed;
	background-color: #ffffff;
}

.innerIssuesDetails ul{
	list-style-type: none;
	margin: auto 0px auto 0px;
	padding-left: 0px;
}

.innerIssuesDetails td{
	text-align: left;
	padding: 5px;
}

.counttd{
	width: 30px;
	text-align:center;
}
.moduletd{
	width: 100%;
}

.teststd{
	width: 70%;
}

.countdiv{
	text-align:center;
}

.testsdiv{
	max-height: 200px;
	word-break: keep-all;
	white-space: nowrap;
 	overflow: scroll;
}

</style>
<script src="Chart.bundle.js"></script>
<script src="chartjs-plugin-datalabels.js"></script>
<script src="jquery-3.3.1.js"></script>
<script src="summaryall.js?v={$TSTAMP}"></script>
</head>
<body>
<div class="header">
	<div><xsl:value-of select="$name"/> - Summary</div>
	<div class="inner_felxbox" style="padding-left:30px;">
		<div><span id="resultTextSpan">Result</span>:</div>
		
		<xsl:variable name="allTests"><xsl:value-of select="sum(.//testcase/testsuite/@tests)"/></xsl:variable>
		<xsl:variable name="allErrors"><xsl:value-of select="sum(//testcase/testsuite/@errors)"/></xsl:variable>
		<xsl:variable name="errorsFailed"><xsl:value-of select="(($allErrors * 100) div $allTests) >= $faliurePercent"/></xsl:variable>
		
		<xsl:choose>
			<xsl:when test="//issuesInfo[@issuesFailed='true'] or $errorsFailed='true'">
				<div class="inner_felxbox test_fail">
					<div class="status_fail"><span id="failedTextSpan">Failed</span></div>
					<div class="inner_felxbox fail_reasons">
						<xsl:if test="//issuesInfo[@issuesFailed='true']">
							<div><span id="kpiPriorityTextSpan">KPI: Testcases contain issues with higher priority than</span>: <xsl:value-of select="//issuesInfo/@failPriority"></xsl:value-of> </div>
						</xsl:if>
						<xsl:if test="$errorsFailed='true'">
							<div><span id="kpiFailure1TextSpan">KPI: More than</span>: <xsl:value-of select="$faliurePercent"/>% <span id="kpiFailure2TextSpan">test errors</span></div>
						</xsl:if>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="status_succes"><span id="acceptableTextSpan">Acceptable</span></div>
			</xsl:otherwise>
		</xsl:choose>
	</div>
</div>
<div id="charts-wrapper" style="width:100%; display:flex; justify-content: space-between; padding-bottom: 20px;">
	<div id="canvas-holder" style="flex-grow:1; max-width:40%;">
		<canvas id="chart-area-tests"></canvas>
	</div>
	<xsl:if test="//issuesInfo">
		<div id="canvas-holder" style="flex-grow:1;max-width:40%;">
			<canvas id="chart-area-issues"></canvas>
		</div>
	</xsl:if>
</div>
<table id="results">
<tr>
<th><span id="tablethBrowserSpan">Browser</span></th>
<th><span id="tablethEnviromentSpan">Environment</span></th>
<th><span id="tablethModuleSpan">Module</span></th>
<th><span id="tablethSuccesSpan">Succes</span></th>
<th><span id="tablethFailureSpan">Failure</span></th>
<th><span id="tablethErrorSpan">Error</span></th>
<th><span id="tablethSumSpan">Sum</span></th>
</tr>
<xsl:for-each select="/summaryall/summary">
<xsl:variable name="href"><xsl:value-of select="@concatDir"/>/summary.html</xsl:variable>
<tr>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/browser"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/environment"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/module"/></a></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@tests)-sum(.//testcase/testsuite/@errors)-sum(.//testcase/testsuite/@failures)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@failures)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@errors)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@tests)"/></td>
</tr>
</xsl:for-each>
<tr class="sumrow">
<td><span id="tableSumRowSpan">Sum:</span></td>
<td></td>
<td></td>
<td class="aright"><sum col="4"/></td>
<td class="aright"><sum col="5"/></td>
<td class="aright"><sum col="6"/></td>
<td class="aright"><sum col="7"/></td>
</tr>
</table>
<xsl:if test="$generate.coverage = 'true'">
	<a href="coverageall_out.html"><span id="coverageReportLinkSpan">Coverage report</span></a>
</xsl:if>
<p><i><b><span id="errorHeaderParagraphSpan">Testfehler</span></b> - <span id="errorParagraphSpan">ist eine Ausfürung der Testszenario, die mit einem nicht erwarteten Ereignis endet (Abbruch oder eine nicht erwartete fehlschlagende Bedingung). 
In meinsten Fällen sind im Fall von 'error' auch die Screenshots mit letztem Browserzustand verfügbar und die detaillierte Stacktraces mit Exception-Details.</span></i></p> 
<p><i><b><span id="failureHeaderParagraphSpan">Fehlschlag</span></b> - <span id="failureParagraphSpan">ist eine Ausfürung der Testszenario, die mit einem erwarteten Problem/Ereignis endet - die Probleme werden in Jira als Tickets erfasst. 
Es dürfen mehrere Failures wärend der Ausfürung einer Testszenario gemeldet werden.</span></i></p>
<xsl:if test="$generate.issuescleanup = 'true'">
	<p><span id="issuesCleanupTextSpan">Die Liste mit Issues (PK-Nummer) ohne Fehlschlag (Kandidaten zur manuellen Bereinigung) befindet sich hier</span>: <a href="issuesall.html"><span id="issuesCleanupLinkSpan">Issues ohne Fehlschlag</span></a></p>
</xsl:if>
<xsl:if test="//issuesInfo">
	<h2><span id="issuesListTextSpan">Issues List:</span></h2>
	<div style=" text-align: right;"><button id="detailedTestsButton" onclick="ShowDetaliedTests()" style="display: inline-block;">Mehr</button> </div>
	<table id="issuesDetails" class="issuesDetails">
		<tr>
			<th class="issuesDetailsId"><span id="issuesDetailsIdThSpan">Id</span></th>
			<th class="issuesDetailsSummary"><span id="issuesDetailsSummaryThSpan">Summary</span></th>
			<th class="issuesDetailsPriority"><span id="issuesDetailsPriorityThSpan">Priority</span></th>
			<th class="issuesDetailsStatus"><span id="issuesDetailsStatusThSpan">Status</span></th>
			<th class="issuesDetailsSprint"><span id="issuesDetailsSprintThSpan">Sprint</span></th>
			<th class="issuesDetailsOccurenceCount"><span id="issuesDetailsCountThSpan">Count</span></th>
			<th class="issuesDetailsTests"><span id="issuesDetailsTestThSpan">Tests</span></th>
		</tr>
		<xsl:for-each select="//issuesInfo/issuesList/issue">
			<tr>
				<td><a href="{jira.url}/{@key}"><xsl:value-of select="@key"/></a></td>
				<td class="summary"><xsl:value-of select="@summary"/></td>
				<td><xsl:value-of select="@priority"/></td>
				<td><xsl:value-of select="@status"/></td>
				<td><div style="word-wrap:break-word;"><xsl:value-of select="@sprint"/></div></td>
				<td><xsl:value-of select="./testlist/@testsnumber"/></td>
				<td>
					<table id="innerIssuesDetails" class="innerIssuesDetails">
					<xsl:for-each select="./testlist/module">
						<tr>
						<xsl:variable name="modulehref"><xsl:value-of select="./@path"/>/summary.html?issue=<xsl:value-of select="../../@key"/></xsl:variable>
						
							<td class="moduletd"><div style="word-wrap:break-word;"><a href="{$modulehref}"><xsl:value-of select="@name"/></a></div></td>
							<td class="counttd"><div class="countdiv"><xsl:value-of select="@testnumber"/></div></td>
							<td class="teststd" style="display:none;"><div class="testsdiv">
									<ul>
									 <xsl:for-each select="./test">
									 	<li><xsl:value-of select="."/></li>
									 </xsl:for-each>
									</ul>
								</div>
							</td>
						</tr>
					</xsl:for-each>
					</table>
				</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:if>

<table id="issues" class="hidden">
<tr>
<xsl:for-each select="//issuesInfo/stats/priorities/*">
<th><xsl:value-of select="local-name()"/></th>
</xsl:for-each>
</tr>
<tr class="colorrow">
<xsl:for-each select="//issuesInfo/stats/priorities/*">
<td class="aright"><xsl:value-of select="@color"/></td>
</xsl:for-each>
</tr>
<tr class="datarow">
<xsl:for-each select="//issuesInfo/stats/priorities/*">
<td class="aright"><xsl:value-of select="."/></td>
</xsl:for-each>
</tr>
</table>

</body>
</html>
</xsl:template>


</xsl:stylesheet>
