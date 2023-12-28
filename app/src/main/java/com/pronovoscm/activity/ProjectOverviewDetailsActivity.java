package com.pronovoscm.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferLogPagerAdapter;
import com.pronovoscm.data.ProjectOverviewProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.ProjectInfoFragment;
import com.pronovoscm.fragments.ProjectTeamFragment;
import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoData;
import com.pronovoscm.model.response.projectteam.TeamData;
import com.pronovoscm.persistence.repository.ProjectOverviewRepository;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

//import com.pronovoscm.adapter.EquipmentCategoryPopupAdapter;

public class ProjectOverviewDetailsActivity extends BaseActivity {
    @Inject
    ProjectOverviewProvider mProjectOverviewProvider;
    @Inject
    ProjectOverviewRepository mProjectOverviewRepository;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.lineView)
    View lineView;
    private TransferLogPagerAdapter mSectionsPagerAdapter;
    private int projectID;
    private ProjectOverviewRequest projectOverviewRequest;
    private ProjectInfoFragment projectInfoFragment;
    //private ProjectInHouseResourcesFragment projectInHouseResourcesFragment;
    private ProjectTeamFragment projectTeamFragment;
    // private ProjectSubcontractorsFragment projectSubcontractorsFragment;
    private LoginResponse loginResponse;
    private int userId;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_log_detail_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        lineView.setVisibility(View.GONE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userId = loginResponse.getUserDetails().getUsers_id();
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getIntent().getStringExtra("project_name"));
        projectID = getIntent().getIntExtra("project_id", 0);
        setupViewPager();
        projectOverviewRequest = new ProjectOverviewRequest(projectID);

        callProjectInfoAPI();
       /* if (mProjectOverviewRepository.getProjectResources(userId, projectID) == null) {
            callProjectResourceAPI();
        }*/
        if (mProjectOverviewRepository.getProjectTeam(userId, projectID) == null) {
            callProjectTeamModifiedAPI();
        }
       /* if (mProjectOverviewRepository.getProjectSubcontractors(userId, projectID) == null) {
            callProjectSubcontractorsAPI();
        }*/
    }
/*
    private void callProjectResourceAPI() {
        mProjectOverviewProvider.getProjectResources(projectOverviewRequest, loginResponse, new ProviderResult<ResourceData>() {
            @Override
            public void success(ResourceData result) {
                if (projectInHouseResourcesFragment != null) {
                    projectInHouseResourcesFragment.refreshData(result);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }
        });
    }*/


    /*private void callProjectTeamAPI() {
        mProjectOverviewProvider.getProjectTeam(projectOverviewRequest, loginResponse, new ProviderResult<TeamData>() {

            @Override
            public void success(TeamData result) {
                if (projectTeamFragment != null) {
                    projectTeamFragment.refreshData(result);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }

            @Override
            public void failure(String message) {

                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);
            }
        });
    }*/

    private void callProjectTeamModifiedAPI() {
        mProjectOverviewProvider.getProjectTeamModifiedApi(projectOverviewRequest, loginResponse, new ProviderResult<TeamData>() {

            @Override
            public void success(TeamData result) {
                if (projectTeamFragment != null) {
                    projectTeamFragment.refreshData(result);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }

            @Override
            public void failure(String message) {

                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);
            }
        });
    }

/*

    private void callProjectSubcontractorsAPI() {
        mProjectOverviewProvider.getProjectSubcontractors(projectOverviewRequest, loginResponse, new ProviderResult<SubcontractorData>() {
            @Override
            public void success(SubcontractorData result) {
                if (projectSubcontractorsFragment != null) {
                    projectSubcontractorsFragment.refreshData(result);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }
        });
    }
*/

    /*
       private void callProjectInfoAPI() {
            mProjectOverviewProvider.getProjectInfo(projectOverviewRequest, loginResponse, new ProviderResult<Info>() {
                @Override
                public void success(Info result) {
                    projectInfoFragment.refreshData(result);
                }

                @Override
                public void AccessTokenFailure(String message) {

                    CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

                }
            });
     */
    private void callProjectInfoAPI() {
        //  Log.d("siddesh", "Activity callProjectInfoAPI: ");
        mProjectOverviewProvider.getDynamicProjectInfo(projectOverviewRequest, loginResponse, new ProviderResult<ProjectOverviewInfoData>() {
            @Override
            public void success(ProjectOverviewInfoData result) {
                projectInfoFragment.refreshData(result);
                //  Log.d("siddesh", "callProjectInfoAPI success: projectInfoFragment.refreshData");
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectOverviewDetailsActivity.this);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {

            offlineTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    public void setupViewPager() {
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        mSectionsPagerAdapter = new TransferLogPagerAdapter(getSupportFragmentManager());
        lineView.setVisibility(View.VISIBLE);
        TabLayout.Tab pickUpTab = tabLayout.newTab();
        pickUpTab.setText("   Info   ");
        tabLayout.addTab(pickUpTab);
        projectInfoFragment = new ProjectInfoFragment();
        mSectionsPagerAdapter.addFragment(projectInfoFragment, "info");


       /* TabLayout.Tab dropOffTab = tabLayout.newTab();
        dropOffTab.setText("   In-House Resources   ");
        tabLayout.addTab(dropOffTab);
        projectInHouseResourcesFragment = new ProjectInHouseResourcesFragment();
        mSectionsPagerAdapter.addFragment(projectInHouseResourcesFragment, "In-House Resources");*/

        TabLayout.Tab infoTab = tabLayout.newTab();
        infoTab.setText("   Project Team   ");
        tabLayout.addTab(infoTab);
        projectTeamFragment = new ProjectTeamFragment();
        mSectionsPagerAdapter.addFragment(projectTeamFragment, "Project Team");


      /*  TabLayout.Tab equipmentTab = tabLayout.newTab();
        equipmentTab.setText("   Project Subcontractors   ");
        tabLayout.addTab(equipmentTab);
        projectSubcontractorsFragment = new ProjectSubcontractorsFragment();
        mSectionsPagerAdapter.addFragment(projectSubcontractorsFragment, "Project Subcontractors");

*/
        //mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
                if (i == 0) {
                    //     Log.d("SIDDESH", "addOnPageChangeListener callProjectResourceAPI onPageSelected: i "+i);
                    callProjectInfoAPI();
                } else {
                    projectInfoFragment.setRecylerviewVisibilityGone();
                }
                /*if (i == 1) {
                    callProjectResourceAPI();
                }*/
                if (i == 1) {
                    callProjectTeamModifiedAPI();
                }
               /* if (i == 3) {
                    callProjectSubcontractorsAPI();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //  Log.d("SIDDESH", "addOnTabSelectedListener : onTabSelected  tab "+tab);
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    //   Log.d("", "addOnTabSelectedListener callProjectResourceAPI onTabSelected: i "+tab.getPosition());
                    callProjectInfoAPI();
                } else {
                    projectInfoFragment.setRecylerviewVisibilityGone();
                }
               /* if (tab.getPosition() == 1) {

                    callProjectResourceAPI();
                }*/
                if (tab.getPosition() == 1) {
                    callProjectTeamModifiedAPI();
                }
               /* if (tab.getPosition() == 3) {
                    callProjectSubcontractorsAPI();
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(mSectionsPagerAdapter);
        //viewPager.setOffscreenPageLimit(equipmentSubCategoriesMasterList.size());
     /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });*/
        mSectionsPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (projectInfoFragment != null) {
            projectInfoFragment.refreshAdapter();
        }
       /* if (projectInHouseResourcesFragment != null) {
            projectInHouseResourcesFragment.refreshAdapter();

        }*/
        if (projectTeamFragment != null) {
            projectTeamFragment.refreshAdapter();

        }
      /*  if (projectSubcontractorsFragment != null) {

            projectSubcontractorsFragment.refreshAdapter();
        }*/
    }
}
