document.getElementById("userselected").addEventListener('click', userSelected);

function userSelected(){
	var url = localStorage.getItem("passgen_url");
	var urlParts = url.replace('http://','').replace('https://','').replace('www.','').split(/[/?#]/);
	var domain = urlParts[0];
	var selectOptions = document.getElementById("userselect");
	var optedValue = selectOptions[selectOptions.options.selectedIndex].value;
	var deviceid = localStorage.getItem("passgen_device_id");
	var url = domain;
	url = url.split(".");
	url = url[0];
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
				hidethis("c-page3");
				hidethis("c-page4");
				showthis("c-page2");
				invertedTheme();
			}else if(parseInt(resp.error_code) === 100){
				hidethis("c-page1");
				hidethis("c-page2");
				hidethis("c-page3");
				normalTheme();
				document.getElementById("userpasswordfield").value = resp.data[0];
				showthis("c-page4");
			}
		}
	}
	xmlhttp.open("GET","http://localhost/passgen/api/v3/ext_get_password.php?device_id="+deviceid + "&url="+url + "&user_name="+optedValue,true);
	xmlhttp.send(deviceid);
}
function getUserNames(){
	var deviceid = localStorage.getItem("passgen_device_id");
	var url = localStorage.getItem("passgen_url");
	var urlParts = url.replace('http://','').replace('https://','').replace('www.','').split(/[/?#]/);
	var domain = urlParts[0];
	url = domain;
	url = url.split(".");
	url = url[0];
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
	xmlhttp.open("GET","http://localhost/passgen/api/v3/ext_search_user.php?device_id="+deviceid + "&url="+url,true);
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