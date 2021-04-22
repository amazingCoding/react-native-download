import { NativeModules } from 'react-native';

const { Download } = NativeModules
export const DownloadFile = (url: string, name: string): Promise<boolean> => {
  return new Promise((resolve) => {
    if (url.startsWith('http') || url.startsWith('https')) {
      Download.downloadFile(url, name, (res: any) => {
        if (res) resolve(true)
        resolve(false)
      })
    }
    else resolve(false)
  })
}
