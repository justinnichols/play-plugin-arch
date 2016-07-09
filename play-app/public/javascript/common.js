function startLadda(element) {
    if (element) {
        var ladda = Ladda.create(element);
        ladda.toggle();
        return ladda;
    } else {
        return undefined;
    }
}

function stopLadda(ladda) {
    if (ladda) {
        ladda.stop();
    }
}

function isNumeric(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57)){
        return false;
    }

    return true;
}

function generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
}

function isMobile() {
    return Modernizr.touchevents;
}

function createSelect2Dropdown(selector, props){
    if(!isMobile()){
        selector.select2(props);

        return true;
    }

    return false;
}