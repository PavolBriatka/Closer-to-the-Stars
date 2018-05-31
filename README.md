# Closer-to-the-Stars
<p>This Android application is designed for all astronomy and astrophysics enthusiasts or simply for all people who like to observe 
the beauty of the infinite universe that surrounds us.</p><p>The application communicates with the NASA APOD (Astronomy Picture Of the Day)
database and brings the user a new image (or video) every day along with its description. Moreover, the application shows an image gallery
which lists either the latest images or a batch of random images. The size of the gallery along with the loading preference is entirely 
up to the user (the size of the gallery is limited to max. 50 items though).</p>
<p>The user can click on an image in the gallery and project it to the main view. Upon clicking on the picture in the main view, the detail 
page opens where the user can read the description, download the image or share it online. A simple click on the image on the detail page
allows the user to toggle the fullscreen mode.</p>
<p>The application is optimized for the portrait and landscape modes for both, mobile phones and tablets (tablet layouts are slightly 
different but the features remain the same).</p>
<p>NOTES:<br>This application uses NASA and YouTube player APIs which require API keys. While you don't necessarily need to register for 
the NASA API key (code works with a demo key) you need to register at least for the YouTube API key at <a href="code.google.com/apis/console">
code.google.com/apis/console</a> (YouTube Data API). After that, please copy and paste the key to the <i>PlayerConfig.java</i> file.</p>
<p>If you wish to register for the NASA API key as well you can do so <a href="https://api.nasa.gov/index.html#apply-for-an-api-key">here</a>
and paste it to the <i>ReceiveMasterPictureTask.java</i> and <i>GalleryFragment.java</i> files instead of "DEMO_KEY".</p>
<p>DISCLAIMER: I'm not responsible for the content showed in the app as it is fetched from external resources. Users should be aware that 
some of the images showed are subject to copyright and thus, they should not be re-used for commercial purposes or modified.</p>
<h1>Screenshots</h1>
