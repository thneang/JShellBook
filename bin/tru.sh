#!/bin/bash

#attention si il y a deux repertoire system_2_grp_gb_kl ça enregistrera dans occun a cause d'un bug de ligne
repertoire=`echo \`date\` | tr ' ' '_'`$1
location=~/work/jshellbook/
mkdir -p $location/messauvegardes/$repertoire
	
if [ -e $location/messauvegardes/$repertoire ]
then
	echo $repertoire
fi
cp -r . $location/messauvegardes/$repertoire
