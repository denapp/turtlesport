; Script generated by the Inno Setup Script Wizard.
; Turtle Sport InnoSetUp

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{14B614CA-42E0-4545-A272-B1248809E95D}
AppName=Turtle Sport v2.0
AppVerName=Turtle Sport v2.0
AppVersion=2.0
AppPublisher=TurtleSport
AppPublisherURL=http://turtlesport.sourceforge.net
AppSupportURL=http://turtlesport.sourceforge.net
AppUpdatesURL=http://turtlesport.sourceforge.net
DefaultDirName={pf}\turtlesport
DefaultGroupName=Turtle Sport
OutputBaseFilename=turtlesport-win-2.0
Compression=lzma2/ultra64
SolidCompression=yes
WizardSmallImageFile=Turtle-55.bmp
WizardImageFile=wizardImage2.bmp
LicenseFile=lgpl-2.1.txt
SetupIconFile=turtleSport.ico
VersionInfoCopyright=Copyright (c) 2009-2017 Turtle Sport


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"
Name: "catalan"; MessagesFile: "compiler:Languages\Catalan.isl"
Name: "Hungarian"; MessagesFile: "compiler:Languages\Hungarian.isl"
Name: "swedish"; MessagesFile: "compiler:Languages\Swedish.isl"
Name: "italian"; MessagesFile: "compiler:Languages\Italian.isl"
Name: "nederlands"; MessagesFile: "compiler:Languages\Dutch.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "..\build\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\Turtle Sport"; Filename: "{app}\turtlesport.exe"
Name: "{group}\{cm:ProgramOnTheWeb,Turtle Sport}"; Filename: "http://turtlesport.sourceforge.net"
Name: "{group}\{cm:UninstallProgram,Turtle Sport}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\Turtle Sport"; Filename: "{app}\turtlesport.exe"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\Turtle Sport"; Filename: "{app}\turtlesport.exe"; Tasks: quicklaunchicon

[Run]
; Unpack largest jars
Filename: "{app}\jre\bin\unpack200.exe"; Parameters:"-r -q ""{app}\jre\lib\rt.pack.gz"" ""{app}\jre\lib\rt.jar"""; Flags: runhidden; StatusMsg: "{cm:UnpackingMessage,rt.jar}";
Filename: "{app}\jre\bin\unpack200.exe"; Parameters:"-r -q ""{app}\turtlesport.pack.gz"" ""{app}\lib\turtlesport.jar"""; StatusMsg: "{cm:UnpackingMessage,turtlesport.jar}"; Flags: runhidden
Filename: "{app}\turtlesport.exe"; Description: "{cm:LaunchProgram,Turtle Sport}"; Flags: nowait postinstall skipifsilent

[CustomMessages]
UnpackingMessage=Unpacking %1...
french.UnpackingMessage=D�compression du fichier %1...

