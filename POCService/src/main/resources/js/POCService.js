// Verify checked

/* [BEGIN] LocalStorage Helper [BEGIN] */
localStorageHelper = (function () {
    var ls = {};
    ls.hasData = function (key) {
        return !!localStorage[key] && !!localStorage[key].length;
    };
    ls.hasKey = function () {
        var item = localStorage.getItem(key);
        return ( item !== null );
    };
    ls.get = function (key, defaultData) {
        if (!this.hasData(key)) {
            if(typeof defaultData !== 'undefined' && defaultData != null) {
                this.set(key, defaultData);
                return defaultData;
            }
            return false;
        }
        var data = localStorage[key];
        try {
            if ( data[0] === '{' || data[0] === '[' )
                return JSON.parse(data);
            return data;
        }
        catch (e) {
            return data;
        }
    };
    ls.set = function (key, value) {
        if ( value === undefined ) $.error("Can't store undefinded value");
        try {
            if ( typeof(value) === 'object' || typeof(value) === 'array' ) {
                value = JSON.stringify(value);
            }

            if ( typeof(value) !== 'string' ) $.error("Can't store unrecognized format value");

            localStorage.setItem(key, value);
        }
        catch (e) { localStorage.setItem(key, value); }
    };
    ls.extend = function (key, value) {
        if (this.hasData(key)) {
            var _value = this.get(key);
            $.extend(_value, JSON.parse(JSON.stringify(value)));
            this.set(key, _value);
        }
        else {
            this.set(key, value);
        }
    };
    ls.remove = function (key) {
        localStorage.removeItem(key);
    };
    return ls;
}());
/* [END] LocalStorage Helper [END] */

/*
 **********************************
 * // Need check function is work //
 **********************************
 */

function loadContentFromIssueKey(issueKey) {
    var loadingIndicator = require('quick-edit/util/loading-indicator');
    loadingIndicator.showLoadingIndicator();
    $.ajax({
        url: AJS.contextPath() + '/secure/AjaxIssueAction!default.jspa?issueKey=' + issueKey + '&decorator=none&prefetch=false&shouldUpdateCurrentProject=false&loadFields=false',
        type: 'get',
        success: function (response) {
            initTabPanels(response);
            initToolBarItems(response);
            initHeader(response);
            JIRA.ViewIssueTabs.domReady($("#activitymodule"));
            loadingIndicator.hideLoadingIndicator();
        }
    });
}

function loadIssueListFromJQL(sqlQuery) {
    var loadingIndicator = require('quick-edit/util/loading-indicator');
    loadingIndicator.showLoadingIndicator();
    $.ajax({
        url: AJS.contextPath() + '/rest/issueNav/1/issueTable',
        type: 'post',
        headers: {
            'X-Atlassian-Token': 'no-check',
            'Accept': 'application/json, text/javascript, */*; q=0.01'
        },
        data: {
            columnConfig: 'explicit',
            layoutKey: 'split-view',
            startIndex: 0,
            jql: sqlQuery
        },
        success: function (response) {
            initIssueFilterContent(response);
            loadingIndicator.hideLoadingIndicator();
        }
    });
}

function initPreparedDataFromJQL(jqlQuery) {
    var ret = "";
    $.ajax({
        url: AJS.contextPath() + '/rest/issueNav/1/issueTable',
        type: 'post',
        headers: {
            'X-Atlassian-Token': 'no-check',
            'Accept': 'application/json, text/javascript, */*; q=0.01'
        },
        data: {
            columnConfig: 'explicit',
            layoutKey: 'split-view',
            startIndex: 0,
            jql: jqlQuery
        },
        success: function (response) {
            WRM._unparsedData["com.atlassian.jira.jira-issue-navigator-components:search-data"] = '{"response": ' + JSON.stringify(response) + ',"jql": "project = CLIENT AND resolution = Unresolved ORDER BY assignee ASC, priority DESC, updated DESC"}';
            initViewDetails();
        }
    });
    // const option = {
    //     'X-Atlassian-Token': 'no-check',
    //     'Accept': 'application/json, text/javascript, */*; q=0.01'
    // };
    // var jsonData = await fetch(AJS.contextPath() + '/rest/issueNav/1/issueTable', option);
    return ret;
}

function resetTabView() {
    AJS.tabs.setup();
}

function initIssueFilterContent(response) {
    var issues = response.issueTable.table;
    $("#crm-issue-list-filter-result").html("");
    for (var i = 0; i < issues.length; ++i) {
        var issue = issues[i];
        var issueType = issue.type;
        var issueHtmlElement = $('<li title="' + issue.summary + '" data-key="' + issue.key + '"><a data-key="' + issue.key + '"\n' +
            '                                                            class="splitview-issue-link" href="' + AJS.contextPath() + '/browse/' + issue.key + '">\n' +
            '                                                        <div data-key="' + issue.key + '">\n' +
            '                                                            <div data-key="' + issue.key + '"><img data-key="' + issue.key + '" alt="' + issueType.name + '" title="' + issueType.description + '"\n' +
            '                                                                      src="' + issueType.iconUrl + '"\n' +
            '                                                                      width="16" height="16"></div>\n' +
            '                                                            <div data-key="' + issue.key + '" class="issue-content-container"><span\n' +
            '                                                                    class="issue-link-key" data-key="' + issue.key + '">' + issue.key + '</span><span\n' +
            '                                                                    class="issue-link-summary" data-key="' + issue.key + '">' + issue.summary + '</span></div>\n' +
            '                                                        </div>\n' +
            '                                                    </a></li>');
        $(issueHtmlElement).click(function (event) {
            onIssueFilterItemClick(event);
        });
        $("#crm-issue-list-filter-result").append(issueHtmlElement);
    }
}

function initTabPanels(response) {
    $("#viewissuesidebar").html("");
    $("#crm-left-panel-groups").html("");
    var panels = response.panels;
    var leftPanels = panels.leftPanels;
    var rightPanels = panels.rightPanels;
    for (var i = 0; i < leftPanels.length; ++i) {
        var thisPanel = leftPanels[i];
        var panelModule = $('<div id="' + thisPanel.id + '" class="module toggle-wrap collapsed">\n' +
            '    <div id="' + thisPanel.id + '-key_heading" class="mod-header">\n' +
            '        <ul class="ops"></ul>\n' +
            '        <a href="#" class="aui-button toggle-title">\n' +
            '            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14">\n' +
            '                <g fill="none" fill-rule="evenodd">\n' +
            '                    <path d="M3.29175 4.793c-.389.392-.389 1.027 0 1.419l2.939 2.965c.218.215.5.322.779.322s.556-.107.769-.322l2.93-2.955c.388-.392.388-1.027 0-1.419-.389-.392-1.018-.392-1.406 0l-2.298 2.317-2.307-2.327c-.194-.195-.449-.293-.703-.293-.255 0-.51.098-.703.293z"\n' +
            '                          fill="#344563"></path>\n' +
            '                </g>\n' +
            '            </svg>\n' +
            '        </a><h4 class="toggle-title">' + thisPanel.label + '</h4></div>\n' +
            '</div>');
        var modContent = $('<div class="mod-content"></div>');
        modContent.append($(thisPanel.html));
        panelModule.append(modContent);
        $("#crm-left-panel-groups").append(panelModule);
    }
    for (var i = 0; i < rightPanels.length; ++i) {
        var thisPanel = rightPanels[i];
        var panelModule = $('<div id="' + thisPanel.id + '" class="module toggle-wrap collapsed">\n' +
            '    <div id="' + thisPanel.id + '-key_heading" class="mod-header">\n' +
            '        <ul class="ops"></ul>\n' +
            '        <a href="#" class="aui-button toggle-title">\n' +
            '            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14">\n' +
            '                <g fill="none" fill-rule="evenodd">\n' +
            '                    <path d="M3.29175 4.793c-.389.392-.389 1.027 0 1.419l2.939 2.965c.218.215.5.322.779.322s.556-.107.769-.322l2.93-2.955c.388-.392.388-1.027 0-1.419-.389-.392-1.018-.392-1.406 0l-2.298 2.317-2.307-2.327c-.194-.195-.449-.293-.703-.293-.255 0-.51.098-.703.293z"\n' +
            '                          fill="#344563"></path>\n' +
            '                </g>\n' +
            '            </svg>\n' +
            '        </a><h4 class="toggle-title">' + thisPanel.label + '</h4></div>\n' +
            '</div>');
        var modContent = $('<div class="mod-content"></div>');
        modContent.append($(thisPanel.html));
        panelModule.append(modContent);
        $("#viewissuesidebar").append(panelModule);
    }
    AJS.tabs.setup();
}

function initToolBarItems(response) {
    var groups = response.issue.operations.linkGroups;
    var primaryGroupJsonData = groups[0].groups;
    var secondaryGroupJsonData = groups[1];
    $("#crm-issue-toolbar-primary").html("");
    $("#crm-issue-toolbar-secondary").html("");
    for (var i = 0; i < primaryGroupJsonData.length; ++i) {
        $("#crm-issue-toolbar-primary").append(getLinkElementFromGroup(primaryGroupJsonData[i]));
        var subGroups = primaryGroupJsonData[i].groups;
        for (var j = 0; j < subGroups.length; ++j) {
            $("#crm-issue-toolbar-primary").append(getGroupElementFromGroup(subGroups[j]));
        }
    }
    $("#crm-issue-toolbar-secondary").append(getLinkElementFromGroup(secondaryGroupJsonData));
    var subGroups = secondaryGroupJsonData.groups;
    for (var j = 0; j < subGroups.length; ++j) {
        $("#crm-issue-toolbar-secondary").append(getGroupElementFromGroup(subGroups[j]));
    }
    $("#edit-issue").click(function (event) {
        showEditForm();
        event.preventDefault();
    })
}

function initHeader(response) {
    var issueJsonObject = response.issue;
    var projectJsonObject = issueJsonObject.project;
    var imageHtmlElement = $('<div class="aui-page-header-image">\n' +
        '                                                        <span id="' + projectJsonObject.id + '"\n' +
        '                                                              class="aui-avatar aui-avatar-large aui-avatar-project jira-system-avatar">\n' +
        '                                                            <span class="aui-avatar-inner">\n' +
        '                                                                <img id="project-avatar"\n' +
        '                                                                     alt="Uploaded image for project: \'' + projectJsonObject.name + '\'"\n' +
        '                                                                     src="http://localhost:2990/jira/secure/projectavatar?pid=10000&amp;avatarId=10203"></span></span>');
    var headerMainHtmlElement = $('<div class="aui-page-header-main"><ol class="aui-nav aui-nav-breadcrumbs" resolved=""><li><a id="project-name-val" href="' + AJS.contextPath() + '/browse/' + projectJsonObject.key + '">' + projectJsonObject.name + '</a></li><li><a class="issue-link" data-issue-key="' + issueJsonObject.key + '" href="' + AJS.contextPath() + '/browse/' + issueJsonObject.key + '" id="key-val" rel="' + issueJsonObject.id + '">' + issueJsonObject.key + '</a></li></ol><h1 id="summary-val" class="editable-field inactive" title="Click to edit">' + issueJsonObject.summary + '<span class="overlay-icon aui-icon aui-icon-small aui-iconfont-edit"></span></h1></div>');
    var headerActionHtmlElement = $('<div class="aui-page-header-actions"><div id="issue-header-pager"></div></div>');
    $("#crm-issue-header").html("");
    $("#crm-issue-header").append(imageHtmlElement);
    $("#crm-issue-header").append(headerMainHtmlElement);
    $("#crm-issue-header").append(headerActionHtmlElement);
}

function getLinkElementFromGroup(groupJsonElement) {
    var groupHtmlElement = $('<div id="opsbar-' + groupJsonElement.id + '" class="aui-buttons pluggable-ops"></div>');
    var linkItems = groupJsonElement.links;
    for (var i = 0; i < linkItems.length; ++i) {
        var linkItem = linkItems[i];
        var iconHtmlElement = '<span class="icon ' + linkItem.iconClass + '"></span> ';
        if (linkItem.iconClass === undefined)
            iconHtmlElement = "";
        var linkHtmlElement = $('<a id="' + linkItem.id + '" title="' + linkItem.title + '" class="aui-button toolbar-trigger ' + linkItem.styleClass + '" href="' + linkItem.href + '">' + iconHtmlElement + '<span class="trigger-label">' + linkItem.label + '</span></a>');
        groupHtmlElement.append(linkHtmlElement);
    }
    return groupHtmlElement;
}

function getGroupElementFromGroup(groupJsonElement) {
    var groupHeader = groupJsonElement.header;
    var groupTriggerHtmlElement = $('<a href="#" id="opsbar-' + groupHeader.id + '" aria-haspopup="true" class="aui-button  aui-dropdown2-trigger" resolved="" aria-controls="opsbar-' + groupHeader.id + '_drop" aria-expanded="false"><span class="dropdown-text">' + groupHeader.label + '</span></a>');
    var groupHtmlElement = $('<aui-dropdown-menu id="opsbar-' + groupHeader.id + '_drop"></aui-dropdown-menu>');
    groupHtmlElement.append(getSectionDropdownHtmlElement(groupJsonElement));
    var groupItems = groupJsonElement.groups;
    for (var i = 0; i < groupItems.length; ++i) {
        groupHtmlElement.append(getSectionDropdownHtmlElement(groupItems[i]));
    }
    groupTriggerHtmlElement.append(groupHtmlElement);
    return groupTriggerHtmlElement;
}

function getSectionDropdownHtmlElement(groupJsonObject) {
    var sectionHtmlElement = $('<aui-section></aui-section>');
    var linkItems = groupJsonObject.links;
    for (var i = 0; i < linkItems.length; ++i) {
        var linkItem = linkItems[i];
        var linkHtmlElement = $('<aui-item-link href="' + linkItem.href + '" id="' + linkItem.id + '" title="' + linkItem.title + '" class="' + linkItem.styleClass + '">' + linkItem.label + '</aui-item-link>');
        sectionHtmlElement.append(linkHtmlElement);
    }
    return linkItems.length > 0 ? sectionHtmlElement : "";
}

function onIssueFilterItemClick(event) {
    event.preventDefault();
    var target = event.target;
    var issueKey = target.getAttribute("data-key");
    loadContentFromIssueKey(issueKey);
}

function showEditForm() {
    JIRA.Forms.createEditIssueForm().asDialog().show();
}

function initViewDetails() {
    var ProjectIssueNavigator = require('jira/projectissuenavigator');
    var Meta = require('jira/util/data/meta');
    var jQuery = require('jquery');
    var projectIssueNavigator = new ProjectIssueNavigator({
        el: jQuery(jQuery('.issue-navigator').get(0)),
        canCreateIssues: true,
        emptyViewContent: true
    });
    projectIssueNavigator.start();

    JIRA.API.getSidebar().done(function (Sidebar) {
        var AUISidebar = Sidebar.getAUISidebar();

        AUISidebar.on('collapse-end', function () {
            projectIssueNavigator.adjustSize();
        });

        AUISidebar.on('expand-end', function () {
            projectIssueNavigator.adjustSize();
        });
    });
    jQuery(function () {
        // Horrible hack. This should go away during JDEV-31262
        jQuery("fieldset.parameters input[title=loggedInUser]").val(Meta.get("remote-user"));
    });
}

function initSearchIssuePage() {
    var Meta = require("jira/util/data/meta");
    JIRA.Issues.Application.start({
        showReturnToSearchOnError: function () {
            return JIRA.Issues.LayoutPreferenceManager.getPreferredLayoutKey() !== "split-view";
        },
        useLog: Meta.get("dev-mode") === true
    });
    JIRA.Issues.GlobalIssueNavCreator.create(jQuery(document), JIRA.Issues.GlobalIssueNavCreator.readInitialData());
}

(function ($, w) {
    $(document).ready(function () {
        // $(".contained-content").css({"overflow-y": "auto", "height": "100%"});

        $(window).on('resize', function () {
            // var wHeight = AJS.$(window).outerHeight();
            // $(".aui-page-panel-content").css("height", wHeight - 50);
        });
        // $(window).trigger('resize');

        var params = loadQueryString();
        if(params.hasOwnProperty('selectedItem') && params['selectedItem'] !== "") {
            $("a.aui-nav-item[href$='selectedItem=" + params['selectedItem']+ "']").css({"background-color": "rgba(9,30,66,.08)", "text-decoration": "none"})
        }
    });
})(AJS.$, window);

function getNumberFormat(valueInput, numberOfDigit) {
    var numberInput;
    if (typeof valueInput != "number")
        numberInput = parseFloat(valueInput);
    else
        numberInput = valueInput;
    var condition = numberInput - Math.trunc(numberInput);
    if (condition > 0) {
        return numberInput.toFixed(numberOfDigit).toString();
    } else if (numberInput == 0) {
        return "";
    } else
        return numberInput.toString();
}

function loadQueryString(){
    var parameters = {};
    var searchString = location.search.substr(1);
    var pairs = searchString.split("&");
    var parts;
    for(i = 0; i < pairs.length; i++){
        parts = pairs[i].split("=");
        var name = parts[0];
        var data = decodeURI(parts[1]);
        parameters[name] = data;
    }
    return parameters;
}
