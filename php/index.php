<?php

/*
*******************************************************************************
* 
* This PHP Script is the entry point of rest.yourdomain.com
* which defines a RESTful service interface implemented with the 'Flight' 
* PHP Framework. 
* 
* ! For demonstrational purposes only !
* 
* Author mailto: Hendrik.Stilke@siemens.com
* 
*******************************************************************************
*/
require 'flight/Flight.php';
 
$GLOBALS['dbhost'] = 'your.host.comes.here';
$GLOBALS['dbuser'] = 'root';
$GLOBALS['dbpass'] = 'guesswhat';
$GLOBALS['dbname'] = 'yesnogame';

//============================
// UTILITY functions first
//============================

/**
 *
 * Query with $query from db and convert into array with column names like in $columnNames
 *
*/ 
function queryArray ($query, $columnNames) 
{
  $ret = array();
  $result = mysql_query($query) or die(mysql_error());

  while($row = mysql_fetch_array($result))
  {
    $rowret = array();
    foreach($columnNames as $colName)
    {
      $rowret[$colName] = $row[$colName];
    }
    $ret[] = $rowret;
  }
  
  return $ret;
}

/**
* Interpreting $str and return 'true' or 'false'
*/
function getBoolean($str)
{
  if (strcmp('true',strtolower($str)) == 0) return 'true'; 
  if (strcmp('false',strtolower($str)) == 0) return 'false'; 
  if (strcmp('1',$str) == 0) return 'true'; 
  if (strcmp('0',$str) == 0) return 'false'; 
  
  return 'false'; 
}

/**
 * Acquire DB connection and set connection to UTF8 encoding
 * 
*/  
function getDBconn()
{
  $dbconn = mysql_connect($GLOBALS['dbhost'], $GLOBALS['dbuser'], $GLOBALS['dbpass']) or die ('Error connecting to database.');
  mysql_select_db($GLOBALS['dbname']) or die('DB schema error.');
  mysql_query("SET character_set_results = 'utf8', character_set_client = 'utf8', character_set_connection = 'utf8', character_set_database = 'utf8', character_set_server = ''utf8", $dbconn);
  
  return $dbconn;
}

//============================
// PATH functions next
//============================

Flight::set('userCols', array('id', 'name'));
Flight::set('voteCols', array('id', 'userId', 'pollId', 'voteValue'));
Flight::set('pollCols', array('id', 'ownerId', 'title', 'question', 'isOpen', 'created'));

//---------
// FIND
//---------

// reset data! convenience url to reset all data
Flight::route('GET /api/resetall', function(){ 
    $query = "DELETE FROM vote WHERE 1";
    mysql_query($query) or die(mysql_error());
    $query = "DELETE FROM poll WHERE 1";
    mysql_query($query) or die(mysql_error());
    $query = "DELETE FROM user WHERE 1";
    mysql_query($query) or die(mysql_error());
    echo "RESET!";
});

// find one
Flight::route('GET /api/user/@id:[0-9]+', function($id){ 
  $query = "SELECT * FROM user WHERE id=$id";
  $ret = queryArray($query, Flight::get('userCols'));
  echo Flight::json($ret[0]);
});

// find one
Flight::route('GET /api/vote/@id:[0-9]+', function($id){
  $query = "SELECT id, user as userId, poll as pollId, vote_value as voteValue FROM vote WHERE id=$id";
  $ret = queryArray($query, Flight::get('voteCols'));
  echo Flight::json($ret[0]);
});

// find one
Flight::route('GET /api/poll/@id:[0-9]+', function($id){
  $query = "SELECT id, owner as ownerId, title, question, is_open as isOpen, created FROM poll WHERE id=$id";
  $ret = queryArray($query, Flight::get('pollCols'));
  echo Flight::json($ret[0]);
});

// find one user by name
Flight::route('GET /api/user/name/@name', function($name){
  $query = "SELECT * FROM user WHERE name like '$name'";
  $ret = queryArray($query, Flight::get('userCols'));
  echo Flight::json($ret[0]);
});

// find one votes by poll and user 
Flight::route('GET /api/vote/user/@userid:[0-9]+/poll/@pollid:[0-9]+', function($userid, $pollid){
  $query = "SELECT id, user as userId, poll as pollId, vote_value as voteValue FROM vote WHERE user=$userid and poll=$pollid";
  $ret = queryArray($query, Flight::get('voteCols'));
  echo Flight::json($ret[0]);
});

// find all votes by poll
Flight::route('GET /api/vote/poll/@pollid:[0-9]+', function($pollid){
  $query = "SELECT id, user as userId, poll as pollId, vote_value as voteValue FROM vote WHERE poll=$pollid";
  $ret = queryArray($query, Flight::get('voteCols'));
  echo Flight::json($ret);
});

// find all
Flight::route('GET /api/user', function(){
  $query = "SELECT * FROM user";
  $ret = queryArray($query, Flight::get('userCols'));
  echo Flight::json($ret);
});

// find all
Flight::route('GET /api/vote', function(){
  $query = "SELECT id, user as userId, poll as pollId, vote_value as voteValue FROM vote";
  $ret = queryArray($query, Flight::get('voteCols'));
  echo Flight::json($ret);
});

// find all
Flight::route('GET /api/poll', function(){
  $query = "SELECT id, owner as ownerId, title, question, is_open as isOpen, created FROM poll";
  $ret = queryArray($query, Flight::get('pollCols'));
  echo Flight::json($ret);
});

//---------
// CREATE
//---------

// create one
Flight::route('POST /api/user', function() {
  $json = Flight::request()->data->json;
  $data = json_decode($json, true);
  
  $name = $data['name'];
  
  if (strlen($name)>0)
  {
    $query = "INSERT INTO user VALUES(NULL,'$name')";
    mysql_query($query) or die(mysql_error());
    $query = "SELECT * FROM user WHERE name like '$name'";
    $ret = queryArray($query, Flight::get('userCols'));
    echo Flight::json($ret[0]);
  } else {
    echo "null";
  }
});

// create one
Flight::route('POST /api/vote', function(){
  $json = Flight::request()->data->json;
  $data = json_decode($json, true);
  
  $user = $data['userId']+0;
  $poll = $data['pollId']+0;
  $votevalue = $data['voteValue']+0;
  
  $query = "INSERT INTO vote VALUES(NULL, $user, $poll, $votevalue)";
  mysql_query($query) or die(mysql_error());
  $query = "SELECT id, user as userId, poll as pollId, vote_value as voteValue FROM vote WHERE user=$user AND poll=$poll";
  $ret = queryArray($query, Flight::get('voteCols'));
  echo Flight::json($ret[0]);
});

// create one
Flight::route('POST /api/poll', function(){
  $json = Flight::request()->data->json;
  $data = json_decode($json, true);
  
  $owner = $data['ownerId'];
  $title = $data['title'];
  $question = $data['question'];
  $is_open = getBoolean($data['isOpen']);
  $created = $data['created'];
  
  $query = "INSERT INTO poll VALUES(NULL, $owner, '$title', '$question', '$is_open', $created)";
  mysql_query($query) or die(mysql_error());
  $query = "SELECT id, owner as ownerId, title, question, is_open as isOpen, created FROM poll WHERE title like '$title'";
  $ret = queryArray($query, Flight::get('pollCols'));
  echo Flight::json($ret[0]);
});

//---------
// UPDATE
//---------
// ! PUT does not work for unknown reasons here. So we circumvent this problem by using a post to a different url instead
Flight::route('POST /api/poll/put', function(){
  $json = Flight::request()->data->json;
  $data = json_decode($json, true);
  
  $id = $data['id'];
  $owner = $data['ownerId'];
  $title = $data['title'];
  $question = $data['question'];
  $is_open = getBoolean($data['isOpen']);
  $created = $data['created'];
  
  $query = "UPDATE poll SET owner=$owner, title='$title', question='$question', is_open='$is_open', created=$created WHERE id=$id";
  mysql_query($query) or die(mysql_error());
  $query = "SELECT id, owner as ownerId, title, question, is_open as isOpen, created FROM poll WHERE title like '$title'";
  $ret = queryArray($query, Flight::get('pollCols'));
  echo Flight::json($ret[0]);
  
});


Flight::route('*', function(){
    echo 'Invalid path requested!';
});

Flight::set('dbConn', getDBconn());

Flight::start();
?>
