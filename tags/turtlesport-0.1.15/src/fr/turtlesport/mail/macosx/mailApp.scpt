#!/usr/bin/osascript
  
tell application "Mail"
  try
    set composeMessage to make new outgoing message at beginning with properties {visible:true,subject:"_subject_", content:"_body_"}
  end try

  make new recipient at end of to recipients with properties {address:"_toAddress_"}

