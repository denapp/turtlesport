#!/usr/bin/osascripttell application "System Events"set UserName to name of the current user as stringend tell   
  
tell application "Mail"
  try
    set composeMessage to make new outgoing message at beginning with properties {visible:true,subject:"_subject_", content:"_body_"}
  end try
tell composeMessage
  make new recipient at end of to recipients with properties {address:"_toAddress_"}  make new attachment with properties {file name:"_filename_"} at after the last paragraph end tellactivate
end tell

