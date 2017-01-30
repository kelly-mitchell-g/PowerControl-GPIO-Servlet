<h1>Main Control</h1><br>
<h2>Light Status</h2>
Current State: ${lState} <br>
Last Changed: ${lLastChange}<br>
Changed From: ${lChangeFrom}<br>
<form action="./Control" method="post">
<button name="lChangeState" type="submit" value="On">On</button>
<button name="lChangeState" type="submit" value="Off">Off</button>
</form>
<h2>Outlet Status</h2>
Current State: ${oState} <br>
Last Changed: ${oLastChange}<br>
Changed From: ${oChangeFrom}<br>
<form action="./Control" method="post">
<button name="oChangeState" type="submit" value="On">On</button>
<button name="oChangeState" type="submit" value="Off">Off</button>
</form>