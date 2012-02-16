%define name turtlesport
%define version VERSION_REPLACED_BY_ANT
%define release 1 

Name:           %{name}
Version:        %{version}
Release:        %{release} 
Summary: Turtle Sport Garmin logbook
Group: Education
URL: http://turtlesport.sourceforge.net/
License: LGPL
Source: http://turtlesport.sourceforge.net/
Packager: Denis Apparicio
Source10:  %{name}.desktop
BuildRoot: %{_builddir}/%{name}-root

%description
Turtle Sport is a free software developed to communicate with Garmin 
fitness products (forerunner and edge).

Turtle Sport retrieves yours training sessions from your Garmin and it makes
 diagram. You can also order your training sessions by category and training 
 mode.  With Turtle Sport, it is also possible to map your GPS training paths 
 in Google Earth. Athlete can configure his profile (weight, heart rate, 
  equipment...) too. You can parameter a critical alert when your equipment
   (running shoes for example) is used during more than a predefined distance.

%install
desktop-file-install --vendor=""                                 \
       --dir=%{buildroot}%{_datadir}/applications/   \
       %{buildroot}%{_datadir}/applications/turtlesport.desktop
desktop-file-validate "%{buildroot}/%{_datadir}/applications/turtlesport.desktop"

%clean
[ "%{buildroot}" != '/' ] && rm -rf %{buildroot}

%files
%defattr(-,root,root)
   /usr/bin/*
   /usr/share/turtlesport/*
   /usr/share/applications/*
   /usr/share/pixmaps/*
%attr(755,root,root) 
   /usr/bin/turtlesport*
%post

%postun
