var hiddenElementIdSuffix = "-body";
var elementHiddenBorderSize = "0px";
var elementShowBorderSize = "0px";
/**
	id 	Is the id of the element that holds the toggle method.
		The element which is hidden/shown may be a child (or other)
		element with the hiddenElementIdSuffix appended to id.
*/
function toggleVisibility(id){
	var toggleElement = document.getElementById(id + hiddenElementIdSuffix);
	
	if(document.getElementById(id).style.border){
		var borderArray = document.getElementById(id).style.border.split(" ");
		var borderColor = ""
		for(var i = 2; i < borderArray.length; i++){
			borderColor += borderArray[i];
		}
		var borderSize = borderArray[0];
		var borderStyle = borderArray[1];
	}
	
	if( toggleElement.style.visibility == 'visible' ){
		toggleElement.style.display = 'none';
		toggleElement.style.visibility = 'hidden';
		document.getElementById(id).style.border = elementHiddenBorderSize + ' ' + borderStyle + ' ' + borderColor;
	}else{
		toggleElement.style.display = 'block';
		toggleElement.style.visibility = 'visible';
		document.getElementById(id).style.border = elementShowBorderSize + ' ' + borderStyle + ' ' + borderColor;
	}
}
