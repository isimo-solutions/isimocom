<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="name" />
	<xsl:param name="testcasesroot" />
	<xsl:param name="jira.url"/>
	<xsl:variable name="firsttestsuite"
		select="/summary/testcase[1]/testsuite" />
	<xsl:output method="xml"></xsl:output>
	<xsl:template match="/">
		<html>
			<head>
				<title>
					Testsuite results
					<xsl:value-of select="$name" />
				</title>
			</head>
<script>
<![CDATA[
function ViewMore(x){
	x.parentElement.classList.toggle("showmorediv");
	if(x.innerText == "More" )x.innerText = "Less";
	else x.innerText = "More";
}


function OptionExist(select, option){
	for(k = 0; k < select.options.length ; k++){
		if(select.options[k].value == option) return true;
	}
	return false;
}

window.onload=function() {
	var table, tr;
	table = document.getElementById('td2');
	var selectResult = document.getElementById('selectResult');
	var selectErrType = document.getElementById('ErrorType');
	var selectErrNum = document.getElementById('ErrorNumber');
	if(selectResult.options.length == 0) selectResult.appendChild(new Option('all','all'));
	if(selectErrNum.options.length == 0)selectErrNum.appendChild(new Option('all','all'));
	if(selectErrType.options.length == 0)selectErrType.appendChild(new Option('all','all'));
	for (i = 1; i < table.rows.length; i++) {
		var tr = table.rows[i];
		var tdResult = tr.cells[2];
		var tdErrType = tr.cells[7];
		if(tdResult){
			if(!OptionExist(selectResult, tdResult.innerText)){ 
			selectResult.appendChild(new Option(tdResult.innerText,tdResult.innerText));
			}
		}
		if(tdErrType && tdErrType.innerText!=""){
			if(!OptionExist(selectErrType, tdErrType.innerText)){
				selectErrType.appendChild(new Option(tdErrType.innerText,tdErrType.innerText));
			}
		}
		var trErrNum = tr.cells[8].getElementsByTagName('table');
		if(trErrNum.length > 0 && trErrNum[0].rows.length > 0){
		trErrNum = trErrNum[0].rows;
			for(j=0; j < trErrNum.length; j++){
				var tdErrNum = trErrNum[j].cells[1];
				if(tdErrNum){
					if(!OptionExist(selectErrNum, tdErrNum.innerText) && tdErrNum.innerText!=""){
					selectErrNum.appendChild(new Option(tdErrNum.innerText,tdErrNum.innerText));
					}
				}
			}
		}
	}
	var issue = getParameterByName("issue");
	if(issue){
		setSelectedValue(selectErrNum, issue)
		Filter();
	}
}

function setSelectedValue(selectObj, valueToSet) {
    for (var i = 0; i < selectObj.options.length; i++) {
        if (selectObj.options[i].text== valueToSet) {
            selectObj.options[i].selected = true;
            return;
        }
    }
}

function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
      results = regex.exec(location.search);
  return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function Filter(){
	var table = document.getElementById('td2');
	var filterResult = document.getElementById('selectResult').value;
	var filterErrType = document.getElementById('ErrorType').value;
	var filterErrNum = document.getElementById('ErrorNumber').value;
	for (i = 1; i < table.rows.length; i++) {
		var ErrNumFound = false;
		var tr = table.rows[i];
		var tdResult = tr.cells[1];
		var tdErrType = tr.cells[6];
		
		var trErrNum = tr.cells[7].getElementsByTagName('table');
		if(trErrNum.length > 0 && trErrNum[0].rows.length > 0){
		trErrNum = trErrNum[0].rows;
			for(j=0; j < trErrNum.length; j++){
				var tdErrNum = trErrNum[j].cells[1];
				if(tdErrNum){
					if(filterErrNum == tdErrNum.innerText) {
						ErrNumFound = true;
						break;
					}
				}
			}
		}		
	
		if((filterResult == "all" || tdResult.innerText == filterResult) && (filterErrType == "all" ||tdErrType.innerText == filterErrType) && (filterErrNum == "all" ||ErrNumFound) ){
		tr.style.display ="";
		} else tr.style.display = "none";
		
	}
}
]]>
</script>
			<style>
.results {
font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
border-collapse: collapse;
width: 100%;
}

.results td, #results th {
border: 1px solid #ddd;
padding: 8px;
}

.results tr:nth-child(even){background-color: #f2f2f2;}

.results tr:hover {background-color: #cdcdcd;}

.results th {
padding-top: 12px;
padding-bottom: 12px;
text-align: center;
background-color: #4CAF50;
color: white;
}

.testnametd {
display: flex;
justify-content: space-between;
}

.testnamediv {
display: flex;
justify-content: center;
flex-direction: column;
text-align: center;
max-width: 300px;
min-width: 50px;
word-wrap: break-word;
}

.timediv{
	width: 80px;
	word-wrap: break-word;
}

.errormessagediv{
max-height: 80px;
overflow: hidden;
}

.showmorediv{
max-height: none;
}

.button{
float: right;
}

}
			</style>
			<body>
				<h2>
					<span id="headerSpan">Testsuite results</span>: <xsl:value-of select="$name" />, 
					 <span id="headerBrowserSpan">Browser</span>:
					<xsl:value-of select="//suiteproperties/browser" />, 
					 <span id="headerEnvSpan">Environment</span>:
					<xsl:value-of select="//suiteproperties/environment" />
				</h2>
				<h3><span id="subHeaderDetailsSpan">Environment details</span>:</h3>
				<table class="results">
					<xsl:for-each
						select="//envproperties/properties/property">
						<tr>
							<td>
								<xsl:value-of select="name" />
							</td>
							<td>
								<xsl:value-of select="value" />
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="//suiteproperties/*">
						<tr>
							<td>
								<xsl:value-of select="name()" />
							</td>
							<td>
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
						<tr>
							<td><span id="successesDetailsSpan">Successes</span>: </td>
							<td><xsl:value-of select="sum(.//testcase/testsuite/@tests)-sum(.//testcase/testsuite/@errors)-sum(.//testcase/testsuite/@failures)"/></td>
						</tr>
						<tr>
							<td><span id="failuresDetailsSpan">Failures</span>: </td>
							<td><xsl:value-of select="sum(.//testcase/testsuite/@failures)"/></td>
						</tr>											
						<tr>
							<td><span id="errorsDetailsSpan">Errors</span>: </td>
							<td><xsl:value-of select="sum(.//testcase/testsuite/@errors)"/></td>
						</tr>
						<tr>
							<td><span id="sumDetailsSpan">Sum</span>: </td>
							<td><xsl:value-of select="sum(.//testcase/testsuite/@tests)"/></td>
						</tr>								
				</table>
				<table id="td2" style="border-style:solid;border-width: medium;"
					class="results">
					<tr>
						<th><span id="scenarioNameResultsSpan">Scenario name</span></th>
						<th><span id="descriptionResultsSpan">Description</span></th>
						<th><span id="resultResultsSpan">Result</span><br/><select id="selectResult" onchange="Filter()"/></th>
						<th><span id="timeResultsSpan">Time</span></th>
						<th><span id="detailsResultsSpan">Details</span></th>
						<th><span id="logResultsSpan">Exec Log</span></th>
						<th><span id="timestampResultsSpan">Timestamp</span></th>
						<th><span id="errorTypeResultsSpan">Error type</span><br/><select id="ErrorType" onchange="Filter()"/></th>
						<th><span id="errorMessageResultsSpan">Error message</span><br/><select id="ErrorNumber" onchange="Filter()"/></th>
						<th><span id="screenshotResultsSpan">Error Screenshot</span></th>
					</tr>
					<xsl:apply-templates select="/summary/testcase" />
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="testcase">
		<tr>
			<td>
				<div class="testnametd">
					<div class="testnamediv">
						<xsl:apply-templates
							select=".//property[@name='scenarioname']/@value" />
					</div>
				</div>
			</td>
			<td>
				<div class="testdesctd">
					<div class="testdescdiv">
						<xsl:value-of select="description"/>
					</div>
				</div>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="testsuite/@failures!=0">
						<span id="failureSpan">Failure</span>
					</xsl:when>
					<xsl:when test="testsuite/@errors!=0">
						<xsl:call-template name="error" />
					</xsl:when>
					<xsl:otherwise>
						<span id="successSpan">Success</span>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="aright">
				<xsl:value-of select="testsuite/@time" />
			</td>
			<td align="center">
				<table style="border:0px"><tr><td><a href="{detailreporttxt}">TXT</a></td></tr></table>
				<table style="border:0px"><tr><td><a href="{detailreport}">HTML</a></td></tr></table>
			</td>
			<td align="center">
				<table style="border:0px"><tr><td><a href="{xmllog}">XML</a></td></tr></table>
				<table style="border:0px"><tr><td><a href="{htmllog}">HTML</a></td></tr></table>
			</td>
			<td><div class="timediv">
				<xsl:value-of select="testsuite/@timestamp" />
			</div></td>
			<td>
				<xsl:value-of
					select="testsuite/testcase/*[self::failure or self::error]/@type" />
			</td>
			<td>
				<xsl:apply-templates select=".//system-out"
					mode="failure" />
				<xsl:variable name="stacktrace" select="testsuite/testcase/*[self::failure or self::error]/@message"/>
				<xsl:if test="$stacktrace!=''">
				<div class="errormessagediv">				
				<button class="button" onclick="ViewMore(this)">More</button>
					<xsl:value-of
						select="$stacktrace" />
				</div>
				</xsl:if>
			</td>
			<td>
				<xsl:apply-templates select="errorscreenshot" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="errorscreenshot">
			<a href="{.}"><span id="screenshotResultsSpan">Error Screenshot</span></a>
	</xsl:template>

	<xsl:template match="system-out" mode="failure">
		<table style="border:0px">
			<xsl:apply-templates select="failure" />
		</table>
	</xsl:template>

	<xsl:template match="failure">
		<tr>
			<td>
				<xsl:value-of select="text()" />
			</td>
			<xsl:if test="issue">
				<td>
					<xsl:apply-templates select="issue" />
				</td>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template name="error">
		<span id="errorSpan">Error</span>
		<xsl:apply-templates select=".//system-out/issue" />
	</xsl:template>

	<xsl:template match="issue">
		<a href="{jira.url}/{.}">
			<xsl:value-of select="." />
		</a>
	</xsl:template>

</xsl:stylesheet>
