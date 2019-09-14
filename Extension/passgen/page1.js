document.getElementById("getpasswordbutton").addEventListener('click', getPasswordClicked);
function getPasswordClicked(){
	var deviceid = localStorage.getItem("passgen_device_id");
	if(deviceid === null){
		setQR();
		hidethis("c-page1");
		showthis("c-page2");
		invertedTheme();
	}else{
		hidethis("c-page1");
		checkifAuthenticated();
	}
	deviceid = sessionStorage.getItem("passgen_device_id");
}
function setQR(){
	deviceid = Math.floor(Math.random() * 1000000000);
	localStorage.setItem("passgen_device_id", deviceid);
	var qrcode = new QRCode(document.getElementById("qrspace"), {
		text: deviceid.toString(10),
		width: 128,
		height: 128,
		colorDark : "#000",
		colorLight : "#fff",//rgba(146, 172, 53, 1)",
		correctLevel : QRCode.CorrectLevel.H
	});
}
function checkifAuthenticated(){
	var deviceid = localStorage.getItem("passgen_device_id");
	if(deviceid === null){
		return JSON.parse({"error":true, "error_code":101, "message":"device id is null"});
	}
	if(window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}else{
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if (this.readyState==4 && this.status==200) {
			var resp = JSON.parse(this.response);
			if(parseInt(resp.error_code) === 105){
				setQR();
				hidethis("c-page1");
				showthis("c-page2");
				invertedTheme();
			}else if(parseInt(resp.error_code) === 100){
				getUserNames();
				hidethis("c-page1");
				showthis("c-page5");
				normalTheme();
			}
		}
	}
	xmlhttp.open("GET","http://192.168.43.88/passgen/api/v3/get_access.php?device_id="+deviceid,true);
	xmlhttp.send(deviceid);
}
function getUserNames(){
	var urlll = "";
	chrome.tabs.query({'active': true, 'windowId': chrome.windows.WINDOW_ID_CURRENT},
	function(tabs){
	  urlll = tabs[0].url;
	});
	var deviceid = localStorage.getItem("passgen_device_id");
	var url = urlll;
	var urlParts = url.replace('http://','').replace('https://','').replace('www.','').split(/[/?#]/);
	var domain = urlParts[0];
	url = domain;
	url = url.split(".");
	url = url[0];
	var deviceid = localStorage.getItem("passgen_device_id");
	if(deviceid === null){
		return JSON.parse({"error":true, "error_code":101, "message":"device id is null"});
	}
	if(window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}else{
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if (this.readyState==4 && this.status==200) {
			var resp = JSON.parse(this.response);
			var users = resp.data;
			fillUsersToOptions(users);
		}
	}
	xmlhttp.open("GET","http://192.168.43.88/passgen/api/v3/ext_search_user.php?device_id="+deviceid + "&url="+url,true);
	xmlhttp.send(deviceid);
}
function fillUsersToOptions(users){
	selector = document.getElementById("userselect");
	for(var i = 0; i < users.length; i++){
		var opts = document.createElement("option");
		opts.setAttribute("value", users[i]);
		opts.innerHTML = users[i];
		selector.appendChild(opts);
	}
}
function hidethis(id){
	document.getElementById(id).style.display = "none";
}
function showthis(id){
	document.getElementById(id).style.display = "block";
}
function normalTheme(){
	document.body.style.backgroundColor = "#222";
}
function invertedTheme(){
	document.body.style.backgroundColor = "#fff";
}