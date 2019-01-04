package com.jimmy.pmfetcher;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yangyoujun on 2019/1/4 .
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private NewsData newsData;
    private int currentPage = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(newsData.getPayload().get(position));
    }

    @Override
    public int getItemCount() {
        return newsData == null ? 0 : newsData.getPayload().size();
    }

    void init(final Context context) {
        currentPage = 0;
        loadNextData(context);
    }

    void loadNextData(Context context) {
        loadData(context, ++currentPage);
    }

    private void loadData(final Context context, int page) {
        Request request = new Request.Builder()
                .url(String.format(Locale.getDefault(), Cons.POPULAR, page))
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                loadFailed(context);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    String string = body.string();
                    NewsData data = new Gson().fromJson(string, NewsData.class);
                    if (data != null && data.getPayload() != null) {
                        newDataArrive(data);
                        return;
                    }
                }
                loadFailed(context);
            }

            private void newDataArrive(NewsData data) {
                for (NewsData.Payload payload : data.getPayload()) {
                    String permalink = payload.getPermalink();
                    String newLink = permalink.replace("//www.", "//api.");
                    payload.setPermalink(newLink);
                }
                if (newsData == null) {
                    newsData = data;
                } else {
                    newsData.getPayload().addAll(data.getPayload());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }

            private void loadFailed(final Context context) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void copyAll(Context context) {
        if (newsData == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (NewsData.Payload payload : newsData.getPayload()) {
            builder.append(payload.getPermalink()).append("\n");
        }
        ClipboardManager service = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        service.setText(builder.toString());
        Toast.makeText(context, "copied all", Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final Button copy;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            copy = itemView.findViewById(R.id.copy_url);
        }

        void bind(final NewsData.Payload payload) {

            title.setText(payload.getTitle());

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String permalink = payload.getPermalink();
                    ClipboardManager service = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    service.setText(permalink);
                    Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(payload.getPermalink());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setPackage(Cons.TENCENT_BROWSER);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
