document.getElementById("userpasswordfield").addEventListener('mouseover', showpassword);
document.getElementById("userpasswordfield").addEventListener('mouseout', hidepassword);
document.getElementById("donewithpass").addEventListener('click', donewithall);

function showpassword(){
	document.getElementById("userpasswordfield").type="text";
}
function hidepassword(){
	document.getElementById("userpasswordfield").type="password";
}
function donewithall(){
	hidethis("c-page5");
	hidethis("c-page2");
	hidethis("c-page3");
	hidethis("c-page4");
	showthis("c-page1");
	var selectElem = document.getElementById("userselect");
	removeOptions(selectElem);
	document.getElementById("masterpassword").value = "";
}
function hidethis(id){
	document.getElementById(id).style.display = "none";
}
function showthis(id){
	document.getElementById(id).style.display = "block";
}
function removeOptions(selectbox)
{
    var i;
    for(i = selectbox.options.length - 1 ; i >= 0 ; i--)
    {
        selectbox.remove(i);
    }
}