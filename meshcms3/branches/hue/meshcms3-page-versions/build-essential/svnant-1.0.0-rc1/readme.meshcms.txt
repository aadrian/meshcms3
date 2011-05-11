The following files have been corrected from those distributed in the
pakage:

  * build.properties  (original renamed build.orig.properties)
    - defined username and password for anon svn access
       \ username=guest
       \ password=guest

  * build.xml         (original renamed build.orig.xml)
    - added username and password to <svn ...> task
    - corrected svn repository url
       \ orig: http://svn.collab.net/repos/subclipse/*
       \ corr: http://subclipse.tigris.org/svn/subclipse/* 

This does not impact MeshCMS as the files get the source for svnant
only - but they have been corrected so as not to perpetuate the
distribution of an essentially broken package.

2006-03-24 14:56 Hue Holleran <hueh-mcm@openAction.net>

