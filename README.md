# react-native-download

download

## Installation
add this in package.json "dependencies"

```sh
"react-native-download-file": "git@github.com:amazingCoding/react-native-download.git#a37a89321d16b0ec775af5ddbe4725caf2465716",
```

## Usage

```js
import { DownloadFile } from 'react-native-download';

// ...

setState("downloading")
const res = await DownloadFile('https://static.mokeycode.com/app/1.jpeg', '1.jpeg')
setState(res ? 'success' : 'cancel')

```

## Attention
Android only provides callback functions only for download completion, not for download failure
Android need AndroidManifest
```
<uses-permission android:name="android.permission.ACCESS_DOWNLOAD_MANAGER" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## gif
### IOS
![](./ios.gif)

### android

![](./android.gif)
