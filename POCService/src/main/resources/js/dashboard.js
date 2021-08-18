function handleOpenCreate(issueTypeId, projectId, isSubtask) {
    if (!isSubtask) {
        JIRA.Forms.createCreateIssueForm({
            pid: projectId, issueType: issueTypeId,
        }).asDialog({windowTitle: 'Create Issue'}).show();
        setTimeout(function () {
            $('#summary').attr('value',  issueIds.toString())
        }, 1000);
    } else {
        JIRA.Forms.createSubtaskForm({
            pid: projectId, issueType: issueTypeId,
        }).asDialog({windowTitle: 'Create Issue'}).show();
        setTimeout(function () {
            $('#summary').attr('value', issueIds.toString())
        }, 1500);
    }
    console.log("sadsad")
    /* AP.require('jira', jira => {
         AP.jira.openCreateIssueDialog(function (issues) {
             alert(issues[0]['fields']['summary']);
         }, {
             pid: 10000,

         });
     });*/

}


/*

function handleAllocate() {
    JIRA.Forms.createCreateIssueForm({pid: 10000, issueType: 10000})
        .asDialog({windowTitle: 'Create Issue'})
        .show();
    ;
}

function handlePayment() {
    JIRA.Forms.createCreateIssueForm({pid: 10000, issueType: 10004})
        .asDialog({windowTitle: 'Create Issue'})
        .show();
    ;
}*/

let issueIds = [];

function handleChooseAllocate(issueId) {
    let isCheck = false;
    for (let i = 0; i < issueIds.length; i++) {
        if (issueIds[i] === issueId) {
            const a1 = issueIds.slice(0, i);
            const a2 = issueIds.slice(i + 1, issueIds.length);
            issueIds = a1.concat(a2);
            isCheck = true;
        }
    }
    if (!isCheck)
        issueIds.push(issueId)
    console.log(issueIds)
}

$(document).ready(function () {
    $('#example').DataTable({
        searching: false,
        info: false,
        paging: false,
    });
});
