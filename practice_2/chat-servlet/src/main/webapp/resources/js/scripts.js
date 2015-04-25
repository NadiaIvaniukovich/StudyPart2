var uniqueId = function() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random).toString();
};

var nickname;

var theMessage = function(nickname, text) {
	return {
		author:nickname,
		text:text,
		id: uniqueId()
	};
};

var appState = {
	mainUrl : 'chat',
	messageList:[],
	token : 'TE11EN'
};

function run(){
	var appContainer = document.getElementsByClassName('chat')[0];
	
	appContainer.addEventListener('click', delegateEvent);
	
	nickname = restoreName()||"Гость";
	setCurrentNickname();
    restoreHistory();
}

function createAllMessages(messages) {
	for(var i = 0; i < messages.length; i++)
		addMessageInternal(messages[i]);
}

function setCurrentNickname(){
	var name = document.getElementsByClassName('nickname')[0];
	if(nickname == ''){
		return;
	}
	name.innerText = nickname;
}

function addMessageInternal(message) {
	var item = createItem(message);
	var items = document.getElementById('msgHis');
	var msgList = appState.messageList;

	msgList.push(message);
	items.appendChild(item);
}

function createItem(message){
	var temp = document.createElement('div');
	var htmlAsText = '<li data-task-id="идентификатор">'+
	'<input type="button" value="Редактировать" class="button15 edit-btn"><input type="button" value="Удалить" class="button15 delete-btn"><span>имя</span><span>: </span>текст сообщения<br></li>';

	temp.innerHTML = htmlAsText;
	updateItem(temp.firstChild, message);

	return temp.firstChild;
}

function updateItem(liItem, message){
	
	liItem.setAttribute('data-task-id', message.id);
	liItem.childNodes[2].textContent = message.author;
	liItem.childNodes[4].textContent = message.text;
}

function delegateEvent(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('change-name')){
		onChangeNameButtonClick();
	} else if(evtObj.type === 'click' && evtObj.target.classList.contains('send-msg')){
		onSendButtonClick();
	} else if(evtObj.type === 'click' && evtObj.target.classList.contains('delete-btn')){
		var labelEl = evtObj.target.parentElement;
		if(labelEl.childNodes[2].textContent == nickname){
			deleteMessage(labelEl);
		}
	} else if(evtObj.type === 'click' && evtObj.target.classList.contains('edit-btn')){
		var labelEl = evtObj.target.parentElement;
		if(labelEl.childNodes[2].textContent == nickname){
			changeTextColor(labelEl);
			onEditButtonClick(labelEl);
		}
	}else if(evtObj.type === 'click' && evtObj.target.classList.contains('change-msg')){
		onChangeButtonClick();
	}
}

function onChangeNameButtonClick(){
	var nameText = document.getElementById('name-input');
	
	if(nameText.value == ''){
		return;
	}
	nickname = nameText.value;
	setCurrentNickname();
	storeName(nickname);
	nameText.value = '';
}
	
function onSendButtonClick(){
	messageText = document.getElementsByClassName('msg-input')[0];
	var newMessage = theMessage(nickname, messageText.value);

	if(messageText.value == '')
		return;

	messageText.value = '';
	addMessage(newMessage);
	//storeHistory(messageList);
}

function addMessage(message) {
	post(appState.mainUrl, JSON.stringify(message),function(){
        restoreHistory();
    });
}

var messageText;
var htmlItem;
var sendBtn;

function onEditButtonClick(liItem) {	
	htmlItem = liItem;	
	var textNode = htmlItem.childNodes[4];
	messageText = document.getElementsByClassName('msg-input')[0];
	messageText.value = textNode.textContent;
	sendBtn = document.getElementsByClassName('send-msg')[0];
	if(sendBtn != null){
		changeButton(true);
	}
}

function changeTextColor(liItem){
	var item = document.getElementsByClassName('edit-text')[0];
	if(item){
		item.classList.remove('edit-text');
	}
	
	liItem.classList.add('edit-text');
}

function onChangeButtonClick(){
	var id = htmlItem.attributes['data-task-id'].value;
	var messageList = appState.messageList;
	for(var i = 0; i < messageList.length; i++) {
		if(messageList[i].id != id)
			continue;

		sendBtn = document.getElementsByClassName('change-msg')[0];
        changeButton(false);
		changeMessageText(messageList[i], function(){
			updateItem(htmlItem, messageList[i]);
			htmlItem.classList.remove('edit-text');
			});
		messageText.value = '';

		return;
	}	
}

function changeMessageText(message, continueWith){
	message.text = messageText.value;
	put(appState.mainUrl + '?id=' + message.id, JSON.stringify(message), function() {
		continueWith();
	});
}

function changeButton(flag){
	if(flag){
		sendBtn.classList.remove('send-msg');
		sendBtn.classList.add('change-msg');
	}else{
		sendBtn.classList.remove('change-msg');
		sendBtn.classList.add('send-msg');
	}
}

function deleteMessage(liItem){
	var id = liItem.attributes['data-task-id'].value;
    var messageList = appState.messageList;
	var items = document.getElementById('msgHis');
	
	for(var i = 0; i < messageList.length; i++) {
		if(messageList[i].id != id)
			continue;

        removeMessage(messageList[i], function(){
            messageList.splice(i,1);
            updateToken();
            items.removeChild(liItem);})
		return;
	}
}


function removeMessage(message, continueWith){
    doDelete(appState.mainUrl + '?id=' + message.id, JSON.stringify(message), function() {
        continueWith();
    });
}

function updateToken() {
    appState.token ='TE'+(appState.messageList.length*8+11)+'EN';
}

function storeName(name) {

	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("Nickname", JSON.stringify(name));
}

function restoreHistory(continueWith) {
	var url = appState.mainUrl + '?token=' + appState.token;

	get(url, function(responseText) {
        console.assert(responseText != null);

        var response = JSON.parse(responseText);

        appState.token = response.token;
        createAllMessages(response.messages);

        continueWith && continueWith();
    });
}

function restoreName() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var item = localStorage.getItem("Nickname");

	return item && JSON.parse(item);
}

function defaultErrorHandler(message) {
    console.error(message);
    output("Сервер недоступен.");
    clear();
}

function clear(){
    appState.messageList=[];
    appState.token="TE11EN";
    var items = document.getElementById('msgHis');
    items.innerHTML="";
}

function output(value){
    var output = document.getElementById('status-message');
    output.innerText = value;
}

function isError(text) {
	if(text == "")
		return false;
	
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}

	return !!obj.error;
}

function get(url, continueWith, continueWithError) {
	ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
	ajax('POST', url, data, continueWith, continueWithError);	
}

function put(url, data, continueWith, continueWithError) {
	ajax('PUT', url, data, continueWith, continueWithError);	
}

function doDelete(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
}

function ajax(method, url, data, continueWith, continueWithError) {
	var xhr = new XMLHttpRequest();

	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method || 'GET', url, true);

	xhr.onload = function () {
		if (xhr.readyState !== 4)
			return;

		if(xhr.status != 200) {
			continueWithError('Error on the server side, response ' + xhr.status);
			return;
		}

		if(isError(xhr.responseText)) {
			continueWithError('Error on the server side, response ' + xhr.responseText);
			return;
		}


        output("Сервер доступен.");
		continueWith && continueWith(xhr.responseText);
	};    

    xhr.ontimeout = function () {
    	continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
    	var errMsg = 'Server connection error !\n'+
    	'\n' +
    	'Check if \n'+
    	'- server is active\n'+
    	'- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

window.onerror = function(err) {
	output("Сервер недоступен.");
    clear();
}