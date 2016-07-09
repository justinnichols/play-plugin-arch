/*
// Global controller for messaging
// 1) Put uniquely identifiable message container in DOM. [exmample HTML at bottom of file]
// 2) Populate container with uniquely identifiable generic messages, hidden by default.
//    These will serve as message templates. [exmample HTML at bottom of file]
// 3) Show a generic message supplying the selector query string for the container and the message template. Ex:
//    MessageController.container("#trays-messages").template("#tray-attachment-upload-error").add();
// 4) Clear all visible messages in container before showing another if you want to prevent stacking. Ex:
//    MessageController.container("#trays-messages").template("#tray-attachment-upload-error").replace();
// 5) Show a message, overriding the generic text with something more informative. Ex:
//    MessageController.container("#trays-messages").template("#tray-attachment-upload-error").message("Error: Unsupported file type").replace();
// 6) Show a message, appending details from error response data. Ex:
//    MessageController.container("#trays-messages").template("#tray-attachment-upload-error").error(responseErrorObject).add();
// 7) Manually empty a message container of all visible messages. Ex:
//    MessageController.container("#trays-messages").empty();
// 8) For improved performance, you may pass cached jQuery objects in place of selector strings. Ex:
//    var $myContainer = $("#container");
//    var $myTemplate = $("$template");
//    MessageController.container($myContainer).template($myTemplate).replace();
//   *NOTE: See below for recommended HTML template
*/
var MessageController = (function($, undefined) {
    "ust strict";
    
    var messageObject;
    
    var publicAPI = {
        container: setContainer,
        template: setTemplate,
        message: setMessage,
        error: setErrorObject,
        add: function() { showMessage(false) },
        replace: function() { showMessage(true) },
        empty: emptyContainer
    };
    
    return publicAPI;
    
    function setContainer(containerSelector) {
        if (!messageObject) {
            messageObject = new MessageInterface();
        }
        messageObject.setContainer(containerSelector);
        return this;
    }
    
    function setTemplate(templateSelector) {
        if (!messageObject) {
            messageObject = new MessageInterface();
        }
        messageObject.setTemplate(templateSelector);
        return this;
    }
    
    function setMessage(message) {
        if (!messageObject) {
            messageObject = new MessageInterface();
        }
        messageObject.setMessage(message);
        return this;
    }
    
    function setErrorObject(errorObject) {
        if (!messageObject) {
            messageObject = new MessageInterface();
        }
        messageObject.setErrorObject(errorObject);
        return this;
    }
    
    function showMessage(replaceOthersInContainer) {
        if (!messageObject || !messageObject.$container || !messageObject.$template) {
            throw new Error("Message container and/or template not defined");
        } else {
            var $newMessage = messageObject.$template.clone();
            // Optionally remove visible messages from target container
            if (replaceOthersInContainer) {
                messageObject.$container.children("*").not(".hidden").remove();
            }
            // Optionally override default message text in message template
            if (messageObject.message) {
                $newMessage.find(".messageText").text(messageObject.message);
            }
            // Optionally append error details
            if (messageObject.errorObject) {
                var $formattedErrorHTML = translateErrorObjectIntoHTML(messageObject.errorObject);
                $formattedErrorHTML.insertAfter($newMessage.find(".messageText"));
            }
            messageObject.$container.append($newMessage.removeClass("hidden"));
            // Remove cached messageObject to prevent it from accidentally being shown again
            messageObject = null;
        }
    }
    
    function emptyContainer() {
        if (!messageObject || !messageObject.$container) {
            throw new Error("Message container not defined");
        } else {
            messageObject.$container.children("*").not(".hidden").remove();
            messageObject = null;
        }
    }
    
    /**************************************************/
    /****Interfaces and helpers************************/
    /**************************************************/
    
    function MessageInterface() {
        this.$container;
        this.$template;
        this.message;
        this.errorObject;
        
        this.setContainer = function(containerSelector) {
            if (containerSelector instanceof jQuery) {
                this.$container = containerSelector;
            } else {
                this.$container = $(containerSelector);
            }
        };
        
        this.setTemplate = function(templateSelector) {
            if (templateSelector instanceof jQuery) {
                this.$template = templateSelector;
            } else {
                this.$template = $(templateSelector);
            }
        };
        
        this.setMessage = function(message) {
            this.message = message;
        };
        
        this.setErrorObject = function(errorObject) {
            this.errorObject = errorObject;
        };
    }
    
    // This helper does the heavy lifting for transforming an error response object
    // into formatted HTML for display within an error message
    function translateErrorObjectIntoHTML(errorObject) {
        var errorHTMLString = '<ul class="error-list">';
        if (errorObject.hasOwnProperty("error") && errorObject.error.constructor === Array) {
            $.each(errorObject.error, function(index, errorValue) {
                errorHTMLString += '<li>' + errorValue + '</li>';
            });
        } else {
            errorObject = sortObject(errorObject);
            $.each(errorObject, function(errorKey, errorValue) {
                if (errorValue.constructor === Array) {
                    errorHTMLString += '<li><span style="font-weight: bold;">' + errorKey + '</span><br>';
                    $.each(errorValue, function (eIndex, eVal) {
                        errorHTMLString += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + eVal + '<br>';
                    });
                    errorHTMLString += '</li>';
                } else {
                    errorHTMLString += '<li><span style="font-weight: bold;">' + errorKey + '</span>: ' + errorValue + '</li>';
                }
            });
        }
        
        errorHTMLString += '</ul>';
        return $(errorHTMLString);
    }

    function sortObject(o) {
        var sorted = {},
            key, a = [];

        for (key in o) {
            if (o.hasOwnProperty(key)) {
                a.push(key);
            }
        }

        a.sort();

        for (key = 0; key < a.length; key++) {
            sorted[a[key]] = o[a[key]];
        }
        return sorted;
    }
}(jQuery));

/*
// Recommended HTML template
// <div id="trays-message-container" class="col-6" style="padding:0;">
//     <div id="trays-message-add-error" class="hidden trays-message alert alert-danger" style="padding: 6px;margin-bottom:0.5em;">
//         <span class="close" data-dismiss="alert">&times;</span><span class="messageText">An error occurred while attempting to save tray(s)</span>
//     </div>
// </div>
*/