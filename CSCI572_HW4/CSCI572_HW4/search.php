<?php
include 'SpellCorrector.php';
//used the Peter Norvig spell corrector program for doing spell check
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
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample200');

  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }
}
  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)

?>

<html>
  <head>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
  <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  <script src="stemmer.js"></script>
<script>
   $(document).ready(function(){
        $("#q").keyup(function(){
            var first = $("#q").val().toLowerCase();
			// this is used for getting the list of suggestions for the words after the first word with first word appended
            if(first.lastIndexOf(' ')>=0)
             {
                var last = first.lastIndexOf(' ');
                var second = first.substring(last+1,first.length);
                var first = first.substring(0,last);
                 $.ajax({
              url:'http://localhost:8983/solr/myexample200/suggest',
              data:{'q': second,'wt':'json'},
              dataType:'jsonp',
              jsonp: 'json.wrf',
              success: function(data){
                  var i;  
                  var suggester_data=[];
                  console.log(data['suggest']['suggest'][second]['numFound']);
                 var stems=[];
                 for(i=0; i<data['suggest']['suggest'][second]['numFound'];i++)
                      { 
                          word = first+' ';
                         
                          var x=stemmer(data['suggest']['suggest'][second]['suggestions'][i]['term']);
                          if(stems.indexOf(x)==-1)
			{                          
                          stems.push(x);
 word += (data['suggest']['suggest'][second]['suggestions'][i]['term']);
                          suggester_data.push(word);
			}

                          
                      }
                  $("#q").autocomplete({
                     source: suggester_data
                  }); 
              }});
             }
			 // this is used for getting list of suggestions for the first word
             else
             {
              $.ajax({
              url:'http://localhost:8983/solr/myexample200/suggest',
              data:{'wt':'json', 'q':first},
              dataType:'jsonp',
              jsonp: 'json.wrf',
              success: function(data){
                  var i;  
                  var suggester_data =[];
                  var stems=[];
                  for(i=0; i<data['suggest']['suggest'][first]['numFound'];i++)
                      { 
                         
                          var x= stemmer(data['suggest']['suggest'][first]['suggestions'][i]['term']);
                          
                          if(stems.indexOf(x)==-1)
{                          
                          stems.push(x);
word =(data['suggest']['suggest'][first]['suggestions'][i]['term']);
                          
                          suggester_data.push(word);
}
                         
                          
                          
                      }
                  $("#q").autocomplete({
                     source: suggester_data 
                  });
              }});
             }
             
        });
         
    });
  </script>
</head>
    <title>PHP Solr Client Example</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="submit" name="submitx"/>
      <br>
      <input id="c" name="c" type="radio" value="page_rank" checked="checked"> Sort According to Page Rank Algorithm <br>
      <input id="c" name="c" type="radio" value="default" checked="checked"> Sort According to Default Solr Algorithm<br> 
    </form>

<?php

if($query)
{
try
  {
    if(isset($_GET['submitx']))
     {  
        ini_set('memory_limit','-1');
        $query1=strtolower($query);
        $words=explode(" ",$query1);
        $correctwords=array();
        foreach($words as $word)
{
        $correctwords[]=SpellCorrector::correct($word);
}
        $x=join(" ",$correctwords);
        if($x!=$query1)
        {  
        if($search_algo == "page_rank")
{// this is used for getting the corrected query if pagerank is selected 
        echo "Did you mean :<a href='http://localhost/search10.php?q=$x&submitx=Submit+Query&c=page_rank'>$x</a>";
        }
       else 
{// this is used for getting the corrected query if solr is selected
        echo "Did you mean :<a href='http://localhost/search10.php?q=$x&submitx=Submit+Query&c=default'>$x</a>";
        }
 
    }
 }
    if($search_algo == "page_rank") {
       
       $results = $solr->search($query,0,$limit,array('sort' => 'pageRankFile desc'));
	
    } 
     else {
       $results = $solr->search($query,0,$limit);
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

// display results
if ($results)
{
  $total = (int) $results->response->numFound;

echo "<p>Number of Documents Found:".$total."</p>";
  
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
$link2='http://www.keck.usc.edu/'.substr($id,31);
if(file_get_contents($link2)==false){
$link2='http://keck.usc.edu/'.substr($id,31);
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