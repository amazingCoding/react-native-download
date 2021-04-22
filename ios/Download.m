#import "Download.h"
#import <UIKit/UIKit.h>
#import <React/RCTConvert.h>
#import <React/RCTBridge.h>
#import <React/RCTUtils.h>

@interface Download () <UIDocumentPickerDelegate, UIAdaptivePresentationControllerDelegate>
@property (nonatomic,strong)NSURLSessionDownloadTask *task;
@property (nonatomic,strong)RCTResponseSenderBlock callback;
@end
@implementation Download

RCT_EXPORT_MODULE()
RCT_EXPORT_METHOD(downloadFile:(NSString *)url name:(NSString *)name callback:(RCTResponseSenderBlock)callback){
  self.task =  [NSURLSession.sharedSession downloadTaskWithURL:[NSURL URLWithString:url] completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
    if(error){
      callback(@[[NSNull null]]);
      return;
    }
    // move file
    NSString *docDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0];
    NSString *filePath = [docDir stringByAppendingPathComponent:name];
    NSError *err = nil;
    if ( [[NSFileManager defaultManager] fileExistsAtPath:filePath] ) {
        [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
    }
    [[NSFileManager defaultManager] copyItemAtURL:location toURL:[NSURL fileURLWithPath:filePath] error:&err];
    if(err){
      callback(@[[NSNull null]]);
      return;
    }
    NSCharacterSet *encodeUrlSet = [NSCharacterSet URLQueryAllowedCharacterSet];
    NSString *fileName = [filePath stringByAddingPercentEncodingWithAllowedCharacters:encodeUrlSet];
    NSURL *fileURL = [NSURL URLWithString:[NSString stringWithFormat:@"file://%@",fileName]];
    if(fileURL == nil){
       callback(@[[NSNull null]]);
       return;
    }
    self.callback = callback;
    dispatch_async(dispatch_get_main_queue(), ^{
      UIDocumentPickerViewController *documentPicker = [[UIDocumentPickerViewController alloc] initWithURL:fileURL inMode:UIDocumentPickerModeExportToService];
      documentPicker.delegate = self;
      documentPicker.modalPresentationStyle = UIModalPresentationFormSheet;
      UIViewController *rootViewController = RCTPresentedViewController();
      [rootViewController presentViewController:documentPicker animated:YES completion:nil];
    });
    
  }];
  [self.task resume];
  
}
- (void)documentPickerWasCancelled:(UIDocumentPickerViewController *)controller{
  if(self.callback){
    self.callback(@[[NSNull null]]);
  }
}
- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(NSArray<NSURL *> *)urls{
  if(self.callback){
    self.callback(@[@"success"]);
  }
}
- (void)dealloc{
  if(_callback){
    _callback = nil;
  }
  if(_task){
    [_task cancel];
  }
}

@end
