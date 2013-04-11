<!DOCTYPE html>
<html>
<head>
    <r:require modules="angular"/>
    <r:layoutResources/>
</head>

<body ng-app="Controller">
<div ng-controller="mainCtrl">
    <p>{{today|date:"yyyy-MM-dd"}}</p>
    <p>{{users.length}} users.</p>
    <ul ng-repeat="user in users">
        {{user.name|uppercase}} {{user.score|number:4}}
    </ul>
</div>
</body>
<r:layoutResources/>
<r:external file="cs/controller.coffee"/>
</html>