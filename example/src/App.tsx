import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { DownloadFile } from 'react-native-download';

export default function App() {
  const [state, setState] = React.useState('');

  React.useEffect(() => {

  }, []);
  const download = React.useCallback(async () => {
    if (state === "downloading") return
    setState("downloading")
    const res = await DownloadFile('https://static.mokeycode.com/app/1.jpeg', 'test.pdf')
    setState(res ? 'success' : 'cancel')
  }, [state])

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={download} style={styles.btn}>
        <Text>download</Text>
      </TouchableOpacity>
      <Text style={{ marginTop: 20 }} >{state}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  btn: {
    width: '80%',
    height: 50,
    backgroundColor: '#ff00ff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
