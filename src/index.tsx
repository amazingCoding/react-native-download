import { NativeModules } from 'react-native';

type DownloadType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Download } = NativeModules;

export default Download as DownloadType;
