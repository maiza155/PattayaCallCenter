package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListViewPost;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.PostList;
import com.pattaya.pattayacallcenter.webservice.object.PostObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.GetPostObject;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PostListFragment extends Fragment implements AbsListView.OnScrollListener
        , View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener {

    static PostListFragment fragment;
    final RestAdapter restAdapterPost = WebserviceConnector.getInstanceCartdUI();
    final RestFulQueary restFulQuearyPost = restAdapterPost.create(RestFulQueary.class);
    // widget
    ImageButton btn;
    TextView titleTextView;
    int pagesLoader = 1;
    Boolean isFirst = true;
    private AdapterListViewPost adapterListViewCaseResult; //Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList<Integer> postId = new ArrayList<>();
    private ArrayList<PostObject> listDataresult = new ArrayList<>(); //list post
    private ListView listViewDataResult;
    private TextView tv_empty;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public PostListFragment() {
        // Required empty public constructor
    }

    public static PostListFragment newInstance() {
        if (fragment == null) {
            fragment = new PostListFragment();
            BusProvider.getInstance().register(fragment);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View root = inflater.inflate(R.layout.fragment_post, container, false);
        listViewDataResult = (ListView) root.findViewById(R.id.listViewDataResult);
        tv_empty = (TextView) root.findViewById(R.id.empty);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.container_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listViewDataResult.setEmptyView(tv_empty);
        listViewDataResult.setOnScrollListener(this);
        adapterListViewCaseResult = new AdapterListViewPost(getActivity(), listDataresult);
        adapterListViewCaseResult.setFragment(this);
        listViewDataResult.setAdapter(adapterListViewCaseResult);


        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        if (!isFirst) {
            update();
        } else {
            loadPost();
        }
        //

        tv_empty.setOnClickListener(this);
        return root;


    }


    @Subscribe
    public void onSampleEvent(String event) {
        Activity activity = getActivity();
        if (activity != null) {
            if (event.matches("post")) {
                update();
            }
        }

    }

    Boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        } else if (num < 3) {
            return false;
        } else if ((num % 2) == 0 || (num % 3) == 0) {
            return false;
        }
        int tempNotPrime = 5;
        while (tempNotPrime * tempNotPrime <= num) {
            if (num % tempNotPrime == 0 || num % (tempNotPrime + 2) == 0) {
                return false;
            }
            tempNotPrime = tempNotPrime + 6;
        }
        return true;
    }

    void loadPost() {
        final GetPostObject getPostObject = new GetPostObject();
        getPostObject.setPageNo(pagesLoader);
        int itemPerPage;
        if (listDataresult.size() == 0) {
            System.out.println("first page");
            postId = new ArrayList<>();
            pagesLoader = 1;
            itemPerPage = 10;
            getPostObject.setPageNo(pagesLoader);
            getPostObject.setItemPerPage(itemPerPage);
        } else {
            if ((listDataresult.size() % 5) == 0) {
                System.out.println("sub page mod 5");
                pagesLoader = (listDataresult.size() / 5) + 1;
                itemPerPage = 5;
            } else if ((listDataresult.size() % 6) == 0) {
                System.out.println("sub page mod 6");
                pagesLoader = (listDataresult.size() / 6) + 1;
                itemPerPage = 6;
            } else if ((listDataresult.size() % 7) == 0) {
                System.out.println("sub page mod 7");
                pagesLoader = (listDataresult.size() / 7) + 1;
                itemPerPage = 7;
            } else if ((listDataresult.size() % 8) == 0) {
                System.out.println("sub page mod 8");
                pagesLoader = (listDataresult.size() / 8) + 1;
                itemPerPage = 8;
            } else if ((listDataresult.size() % 9) == 0) {
                System.out.println("sub page mod 9");
                pagesLoader = (listDataresult.size() / 9) + 1;
                itemPerPage = 9;
            } else {
                System.out.println("sub page mod other");
                pagesLoader = 2;
                itemPerPage = listDataresult.size();
            }

            getPostObject.setPageNo(pagesLoader);
            getPostObject.setItemPerPage(itemPerPage);
        }

        tv_empty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                restFulQuearyPost.getPost(getPostObject, new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response2) {
                        //Try to get response body


                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String JsonConvertData = "{data:" + sb.toString() + "}";
                        PostList Object = new Gson().fromJson(JsonConvertData, PostList.class);


                        ArrayList tempList = new ArrayList();

                        if (Object.getData().size() > 0) {
                            pagesLoader++;
                            for (PostObject e : Object.getData()) {
                                if (!postId.contains(e.getPostId())) {
                                    postId.add(e.getPostId());
                                    tempList.add(e);
                                }
                            }

                            adapterListViewCaseResult.addItem(tempList);
                            listDataresult = adapterListViewCaseResult.getListData();


                        }

                        Activity activity = getActivity();
                        if (activity != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    if (adapterListViewCaseResult.getCount() == 0) {
                                        tv_empty.setVisibility(View.VISIBLE);

                                    }
                                    isFirst = false;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }



                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("Post error = [" + error + "]");
                        Activity activity = getActivity();
                        if (activity != null) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (adapterListViewCaseResult.getCount() == 0) {
                                        tv_empty.setVisibility(View.VISIBLE);

                                    }

                                    progressBar.setVisibility(View.GONE);
                                    isFirst = false;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });

                        }


                    }
                });


                return null;
            }
        }.execute();


    }


    void update() {
        if (listDataresult.size() == 0) {
            postId = new ArrayList<>();
        }
        final GetPostObject getPostObject = new GetPostObject();
        getPostObject.setPageNo(1);
        restFulQuearyPost.getPost(getPostObject, new Callback<Response>() {
            @Override
            public void success(Response result, Response response2) {
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {

                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                    String line;

                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList tempList = new ArrayList();

                String JsonConvertData = "{data:" + sb.toString() + "}";

                PostList Object = new Gson().fromJson(JsonConvertData, PostList.class);
                if (Object.getData().size() > 0) {
                    for (PostObject e : Object.getData()) {
                        if (!postId.contains(e.getPostId())) {
                            postId.add(e.getPostId());
                            tempList.add(e);
                        }
                    }

                }

                if (tempList.size() > 0) {
                    adapterListViewCaseResult.addItemUpdate(tempList);
                    listDataresult = adapterListViewCaseResult.getListData();
                }
                mSwipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void failure(RetrofitError error) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    void setActionBar(Context actionBar) {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(actionBar);
        View v = inflater.inflate(R.layout.custom_actionbar_search, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        titleTextView.setText(getResources().getString(R.string.post));
        titleTextView.setPadding(40, 0, 0, 0);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MemberMainActivity) actionBar).getSupportActionBar().setCustomView(v);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        setActionBar((ActionBarActivity) getActivity());
        inflater.inflate(R.menu.menu_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_post:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                getActivity().startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (firstVisibleItem == 0) {
            // check if we reached the top or bottom of the list
            View v = listViewDataResult.getChildAt(0);
            int offset = (v == null) ? 0 : v.getTop();
            if (offset == 0) {
                return;
            }
        }

        if (listViewDataResult.getLastVisiblePosition() == listViewDataResult.getAdapter().getCount() - 1
                && listViewDataResult.getChildAt(listViewDataResult.getChildCount() - 1).getBottom() <= listViewDataResult.getHeight()) {
            System.out.println("bottom in ListView ");
            loadPost();
            return;
        }

    }

    @Override
    public void onClick(View v) {
        if (v == tv_empty) {
            loadPost();
        }

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (adapterListViewCaseResult.getCount() == 0) {
            loadPost();
        } else {
            update();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == adapterListViewCaseResult.TAG_EDITOR) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> image = data.getStringArrayListExtra("image");
                String detail = data.getStringExtra("detail");
                int postId = data.getIntExtra("postId", 0);
                System.out.println("" + postId);
                System.out.println("" + detail);
                if (postId > 0) {
                    adapterListViewCaseResult.updateItem(postId, detail, image);
                }


            }

        }
    }
}
