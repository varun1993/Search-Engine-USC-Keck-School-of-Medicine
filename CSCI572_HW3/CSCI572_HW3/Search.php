<?php

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$search_algo = isset($_REQUEST['c'])?$_REQUEST['c'] : "default";
$results = false;


if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('/home/varunmittal/solr-php-client/Apache/Solr/Service.php');

  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample100');

  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try
  {
    if($search_algo == "page_rank") {
       
       $results=$solr->search($query,0,$limit, array('sort' => 'pageRankFile desc'));
	
    } else {
       $results = $solr->search($query, 0, $limit);
    }
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}

?>
<html>
  <head>
    <title>PHP Solr Client Example</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="submit"/>
      <br>
      <input id="c" name="c" type="radio" value="page_rank" checked="checked"> Sort According to Page Rank Algorithm <br>
      <input id="c" name="c" type="radio" value="default" checked="checked"> Sort According to Default Solr Algorithm<br> 
    </form>
<?php

// display results
if ($results)
{
  $total = (int) $results->response->numFound;
  echo "Number of Documents Found:".$total;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
<?php
  // iterate result documents
   foreach ($results->response->docs as $doc)
  {
?>
<?php
 $id=$doc->id;
 $title=$doc->title;
 $date=$doc->creation_date;
 $author=$doc->creator;
 $size=($doc->stream_size)/1024;
 $url=$doc->og_url;
?>
      <li>
<p>
<?php

if(strpos($id,'aspx') !== false) 
{
$link1='http://keck.usc.edu/'.substr($id,30,-5);
if(file_get_contents($link1)==false){
$link1='http://www.keck.usc.edu/'.substr($id,30,-5);
}
echo "<a href='$link1'>Document</a>";
}


else if(strpos($id,'pdf') !== false)
{
$link2='http://www.keck.usc.edu/'.substr($id,30);
if(file_get_contents($link2)==false){
$link2='http://keck.usc.edu/'.substr($id,30);
}
echo "<a href='$link2'>Document</a>";
}



else if(strpos($id,'doc') !== false)
{
$link='http://keck.usc.edu/'.substr($id,30);
if(file_get_contents($link)==false){
$link='http://www.keck.usc.edu/'.substr($id,30);
}
echo "<a href='$link'>Document</a>";
}
 

else if(strpos($id,'html') !== false)
{

if(strpos($id,'index.html') !== false)
{

$link='http://www.keck.usc.edu/'.substr($id,30,-10);
if(file_get_contents($link)==false){
$link='http://keck.usc.edu/'.substr($id,30,-10);
}
echo "<a href='$link'>Document</a>";
}

else 
{
echo "<a href='$url'>Document</a>";
}
}
?>

</p>

<p>

<?php
        if(!empty($title))
{
        echo "Title: ".$title;   
}


       else 
{       
       echo "Title: "."N/A"; 
}
?>
</p>
    
<p>        
<?php
        if(!empty($date))
{
        echo "Date: ".$date;   
}


       else 
{       
       echo "Date: "."N/A"; 
}
?>
</p>

<p>        
<?php
        if(!empty($author))
{
        echo "Author: ".$author;   
}


       else 
{       
       echo "Author: "."N/A"; 
}
?>
</p>

<p>        
<?php
        if(!empty($size))
{
        echo "File Size: ".$size." KB";   
}


       else 
{       
       echo "File Size: "."N/A"; 
}
?>
</p>      

         
      </li>
<?php
  }
?>
    </ol>
<?php
}
?>
  </body>
</html>