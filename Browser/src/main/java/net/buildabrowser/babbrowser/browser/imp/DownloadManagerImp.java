package net.buildabrowser.babbrowser.browser.imp;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.ua.DownloadManager;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class DownloadManagerImp implements DownloadManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManagerImp.class);

  @Override
  public void startDownload(FetchResponse response, String suggestedFilename) {
    String userHome = System.getProperty("user.home");
    Path filePath = Paths.get(userHome, "Downloads", suggestedFilename);

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(filePath.toFile());
    int fileResponse = fileChooser.showOpenDialog(null);
    if (fileResponse == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      startFileDownload(response, selectedFile);
    }
    // TODO: Abort the response otherwise
  }

  private void startFileDownload(FetchResponse response, File selectedFile) {
    LOGGER.info("Starting download from '{}' to '{}'.",
      response.url(), selectedFile);
    ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader)
      response.body().stream().getReader(null);
    try {
      FileChannel channel = FileChannel.open(
        selectedFile.toPath(),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE);
      reader.read(new DownloadReadRequest(
        reader,
        chunk -> CommonUtil.rethrowV(() -> channel.write(chunk)),
        () -> CommonUtil.rethrowV(() ->
          handleFileDownloadComplete(response, selectedFile, channel)),
        err -> CommonUtil.rethrowV(() ->
          handleFileDownloadError(response, selectedFile, channel, err))
        ));
    } catch (IOException e) {
      showFailureWindow(e.getMessage());
      e.printStackTrace();
    }
  }

  private void handleFileDownloadComplete(
    FetchResponse response,
    File selectedFile,
    FileChannel channel
  ) throws IOException {
    LOGGER.info(
      "Successfully downloaded file from '{}' to '{}'.",
      response.url(), selectedFile);
    StringBuilder messageBuilder = new StringBuilder("Successfully downloaded file from '")
      .append(response.url())
      .append("' to '")
      .append(selectedFile.toString())
      .append(".");
    showSuccessWindow(messageBuilder.toString());

    channel.close();
  }

  private void showSuccessWindow(String message) {
    JOptionPane.showMessageDialog(
      null,
      message,
      "File Download Completed",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  private void handleFileDownloadError(
    FetchResponse response,
    File selectedFile,
    FileChannel channel,
    Object err
  ) throws IOException {
    LOGGER.info(
      "Failed to download filed from '{}' to '{}'!",
      response.url(), selectedFile);
    StringBuilder messageBuilder = new StringBuilder("Failed to download file from '")
      .append(response.url())
      .append("' to '")
      .append(selectedFile.toString());
    if (err instanceof Throwable throwable) {
      messageBuilder
        .append(": ")
        .append(throwable.getMessage());
    }
    messageBuilder.append("!");
    showFailureWindow(messageBuilder.toString());

    if (err instanceof Throwable) {
      ((Throwable)err).printStackTrace();
    }

    channel.close();
  }

  private void showFailureWindow(String message) {
    JOptionPane.showMessageDialog(
      null,
      message,
      "File Download Failed",
      JOptionPane.ERROR_MESSAGE
    );
  }
  
}
