<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="name"/>
<xsl:param name="project"/>
<xsl:param name="TSTAMP"/>
<xsl:param name="faliurePercent"/>
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
		<div>Endergebnis: </div>
		
		<xsl:variable name="allTests"><xsl:value-of select="sum(.//testcase/testsuite/@tests)"/></xsl:variable>
		<xsl:variable name="allErrors"><xsl:value-of select="sum(//testcase/testsuite/@errors)"/></xsl:variable>
		<xsl:variable name="errorsFailed"><xsl:value-of select="(($allErrors * 100) div $allTests) >= $faliurePercent"/></xsl:variable>
		
		<xsl:choose>
			<xsl:when test="//issuesInfo[@issuesFailed='true'] or $errorsFailed='true'">
				<div class="inner_felxbox test_fail">
					<div class="status_fail">fehlgeschlagen</div>
					<div class="inner_felxbox fail_reasons">
						<xsl:if test="//issuesInfo[@issuesFailed='true']">
							<div>KPI verletzt: Testfälle enthalten Issues mit höheren Priorität als: <xsl:value-of select="//issuesInfo/@failPriority"></xsl:value-of> </div>
						</xsl:if>
						<xsl:if test="$errorsFailed='true'">
							<div>KPI verletzt: Mehr als <xsl:value-of select="$faliurePercent"/>% Testfallfehler</div>
						</xsl:if>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="status_succes">akzeptabel</div>
			</xsl:otherwise>
		</xsl:choose>
	</div>
</div>
<div id="charts-wrapper" style="width:100%; display:flex; justify-content: space-between; padding-bottom: 20px;">
	<div id="canvas-holder" style="flex-grow:1; max-width:40%;">
		<canvas id="chart-area-tests"></canvas>
	</div>
	<div id="canvas-holder" style="flex-grow:1;max-width:40%;">
		<canvas id="chart-area-issues"></canvas>
	</div>
</div>
<div class="row"><div class="block"><button id="reset">Kriterien zurücksetzen</button></div><div class="block" style="min-width: 50px; height:30px;"></div><div class="block"><input id="changeconfig" type="checkbox" checked="off"/></div><div class="block">Detailansicht</div></div>
<table id="results">
<tr>
<th>Testsuite</th>
<th>Browser</th>
<th>Env (technisch)</th>
<th>Umgebung</th>
<th>Modul</th>
<th>Erfolge</th>
<th>Fehlschläge</th>
<th>Testfehler</th>
<th>Gesamt</th>

<th>Anzahl gemeldeten Probleme</th>
<th>Anzahl unbekannen Probleme</th>
<th>Einzelne Failures</th>
<th>Gemeldete Errors</th>
<th>Nicht gemeldete Errors</th>

<th>Version Kern</th>
<th>Version Core</th>
<th>Version Wm</th>
<th>Mandant ID</th>
</tr>
<xsl:for-each select="/summaryall/summary">
<xsl:variable name="href">../result_<xsl:value-of select="./testcase/teststart"/>_<xsl:value-of select="./suiteproperties/environment"/>_<xsl:value-of select="./suiteproperties/browser"/>_<xsl:value-of select="./suiteproperties/module"/>/<xsl:value-of select="$project"/>/target/summary.html</xsl:variable>
<tr>
<td><a href="{$href}">result_<xsl:value-of select="./testcase/teststart"/>_<xsl:value-of select="./suiteproperties/environment"/>_<xsl:value-of select="./suiteproperties/browser"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/browser"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/environment"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./envproperties/env.title"/></a></td>
<td><a href="{$href}"><xsl:value-of select="./suiteproperties/module"/></a></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@tests)-sum(.//testcase/testsuite/@errors)-sum(.//testcase/testsuite/@failures)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@failures)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@errors)"/></td>
<td class="aright"><xsl:value-of select="sum(.//testcase/testsuite/@tests)"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase//failure[.//issue])"/></td>
<td class="aright" critical="yes"><xsl:value-of select="count(.//testcase//failure[not(.//issue) and not(parent::testcase)])"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase//failure[not(parent::testcase)])"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase/testsuite[(@errors = 1) and .//issue[not(ancestor-or-self::failure)]])"/></td>
<td class="aright" critical="yes"><xsl:value-of select="count(.//testcase/testsuite[(@errors = 1) and not(.//issue[not(ancestor-or-self::failure)])])"/></td>

<td><xsl:value-of select="./envproperties/properties/property[name='version.txtKernVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtCoreVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtWmVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtMandantId']/value"/></td>
</tr>
</xsl:for-each>
<xsl:for-each select="//qftest/testsuites">
<xsl:message>qftest!</xsl:message>
<xsl:variable name="href">../result_<xsl:value-of select="./testcase/teststart"/>_<xsl:value-of select="./suiteproperties/environment"/>_<xsl:value-of select="./suiteproperties/browser"/>_<xsl:value-of select="./suiteproperties/module"/>/<xsl:value-of select="$project"/>/target/summary.html</xsl:variable>
<tr>
<td><!-- <a href="{$href}">result_<xsl:value-of select="./testcase/teststart"/>_<xsl:value-of select="./suiteproperties/environment"/>_<xsl:value-of select="./suiteproperties/browser"/></a> --></td>
<td><a href="{$href}">internetExplorer</a></td>
<td><a href="{$href}"><xsl:value-of select="../@env"/></a></td>
<td><a href="{$href}"><xsl:value-of select="../@env"/></a></td>
<td><a href="{$href}"><xsl:value-of select="../@module"/></a></td>
<td class="aright"><xsl:value-of select="count(.//testcase[@vstate=1])"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase[@vstate=2])"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase[@vstate=3])"/></td>
<td class="aright"><xsl:value-of select="count(.//testcase)"/></td>
<td class="aright">0</td>
<td class="aright">0</td>
<td class="aright">0</td>
<td class="aright">0</td>
<td class="aright">0</td>

<td><xsl:value-of select="./envproperties/properties/property[name='version.txtKernVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtCoreVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtWmVersion']/value"/></td>
<td><xsl:value-of select="./envproperties/properties/property[name='version.txtMandantId']/value"/></td>
</tr>
</xsl:for-each>
<tr class="sumrow">
<td>Gesamt:</td>
<td/>
<td/>
<td/>
<td/>
<td class="aright"><sum col="6"/></td>
<td class="aright"><sum col="7"/></td>
<td class="aright"><sum col="8"/></td>
<td class="aright"><sum col="9"/></td>
<td class="aright"><sum col="10"/></td>
<td class="aright" critical="yes"><sum col="11"/></td>
<td class="aright"><sum col="12"/></td>
<td class="aright"><sum col="13"/></td>
<td class="aright" critical="yes"><sum col="14"/></td>
<td/>
<td/>
<td/>
<td/>
</tr>
</table>
<a href="coverageall_out.html">Testabdeckungsbericht</a>

<p><i><b>Testfehler</b> - ist eine Ausfürung der Testszenario, die mit einem nicht erwarteten Ereignis endet (Abbruch oder eine nicht erwartete fehlschlagende Bedingung). 
In meinsten Fällen sind im Fall von 'error' auch die Screenshots mit letztem Browserzustand verfügbar und die detaillierte Stacktraces mit Exception-Details.</i></p> 
<p><i><b>Fehlschlag</b> - ist eine Ausfürung der Testszenario, die mit einem erwarteten Problem/Ereignis endet - die Probleme werden in Jira als Tickets erfasst. 
Es dürfen mehrere Failures wärend der Ausfürung einer Testszenario gemeldet werden.</i></p>
<p>Die Liste mit Issues (PK-Nummer) ohne Fehlschlag (Kandidaten zur manuellen Bereinigung) befindet sich hier: <a href="issuesall.html">Issues ohne Fehlschlag</a></p>

<h2>Issues List:</h2>
<div style=" text-align: right;"><button id="detailedTestsButton" onclick="ShowDetaliedTests()" style="display: inline-block;">Mehr</button> </div>
<table id="issuesDetails" class="issuesDetails">
	<tr>
		<th class="issuesDetailsId">Id</th>
		<th class="issuesDetailsSummary">Summary</th>
		<th class="issuesDetailsPriority">Priority</th>
		<th class="issuesDetailsStatus">Status</th>
		<th class="issuesDetailsSprint">Sprint</th>
		<th class="issuesDetailsOccurenceCount">Count</th>
		<th class="issuesDetailsTests">Tests</th>
	</tr>
	<xsl:for-each select="//issuesInfo/issuesList/issue">
		<tr>
			<td><a href="https://seproxy.bitmarck-software.de/jira/browse/{@key}"><xsl:value-of select="@key"/></a></td>
			<td class="summary"><xsl:value-of select="@summary"/></td>
			<td><xsl:value-of select="@priority"/></td>
			<td><xsl:value-of select="@status"/></td>
			<td><div style="word-wrap:break-word;"><xsl:value-of select="@sprint"/></div></td>
			<td><xsl:value-of select="./testlist/@testsnumber"/></td>
			<td>
				<table id="innerIssuesDetails" class="innerIssuesDetails">
				<xsl:for-each select="./testlist/module">
					<tr>
					<xsl:variable name="modulehref">../<xsl:value-of select="./@path"/>/summary.html?issue=<xsl:value-of select="../../@key"/></xsl:variable>
					
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



<table id="issues" class="hidden">
<tr>
<th>Blocker</th>
<th>Kritisch</th>
<th>Wichtig</th>
<th>Standard</th>
<th>Gering</th>
<th>Trivial</th>
</tr>
<tr class="datarow">
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Blocker"/></td>
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Kritisch"/></td>
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Wichtig"/></td>
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Standard"/></td>
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Gering"/></td>
<td class="aright"><xsl:value-of select="//issuesInfo/stats/priorities/Trivial"/></td>
</tr>
</table>

</body>
</html>
</xsl:template>


</xsl:stylesheet>
